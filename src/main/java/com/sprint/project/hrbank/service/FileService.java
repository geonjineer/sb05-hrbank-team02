package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.exception.FileStorageException;
import com.sprint.project.hrbank.repository.FileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  @Value("${hrbank.storage.root:./storage}")
  private String storageRoot;

  @Transactional
  public FileResponse upload(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
    }

    final String safeFileName = normalizeFileName(file.getOriginalFilename());
    final String safeContentType = normalizeContentType(file.getContentType());
    final long size = file.getSize();

    // 1) 메타 저장
    File meta = new File(safeFileName, safeContentType, size);
    meta = fileRepository.save(meta);

    // 2) 디스크 저장 (실패 시 메타 롤백)
    final Path root = Path.of(storageRoot);
    try {
      Files.createDirectories(root);
      final Path dest = root.resolve(String.valueOf(meta.getId()));
      try (InputStream in = file.getInputStream()) {
        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
      }
      return new FileResponse(meta.getId(), safeFileName, safeContentType, size);
    } catch (IOException e) {
      fileRepository.deleteById(meta.getId());
      throw new FileStorageException("디스크 저장에 실패했습니다. id: " + meta.getId(), e);
    }
  }

  @Transactional(readOnly = true)
  public FileResponse getMeta(Long id) {
    File meta = fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("파일 메타 정보를 찾을 수 없습니다. id: " + id));
    return new FileResponse(meta.getId(), meta.getFileName(), meta.getContentType(), meta.getSize());
  }

  @Transactional(readOnly = true)
  public FileSystemResource download(Long id) {
    // 메타 존재 확인
    fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("파일 메타 정보를 찾을 수 없습니다. id: " + id));

    Path filePath = Path.of(storageRoot, String.valueOf(id));
    if (!Files.exists(filePath)) {
      throw new FileStorageException("디스크에서 파일을 찾을 수 없습니다. path: " + filePath);
    }
    return new FileSystemResource(filePath);
  }

  // ----- 내부 정규화 유틸 -----

  private String normalizeFileName(String fileName) {
    String n = (fileName == null || fileName.isBlank()) ? "이름없음" : fileName.trim();
    n = n.chars()
        .filter(c -> c >= 32 && c != 127)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    n = n.replaceAll("[\\\\/:*?\"<>|]", "_"); // Windows 금지 문자
    n = n.replaceAll("\\s+", " ");
    if (n.equals(".") || n.equals("..")) n = "file";
    if (n.length() > 255) n = n.substring(0, 255);
    return n;
  }

  private String normalizeContentType(String contentType) {
    if (contentType == null || contentType.isBlank()) {
      return "application/octet-stream";
    }
    String t = contentType.trim().toLowerCase();
    return t.matches("^[\\w!#$&^_.+-]+/[\\w!#$&^_.+-]+$") ? t : "application/octet-stream";
  }

  // 서버에서 생성한 임시 파일을 파일 관리 규칙에 맞게 저장한다.
  // 1. size 체크 -> File meta save
  // 2. storage/{id} 위치로 이동
  @Transactional
  public FileResponse saveLocal(Path tempFile, String fileName, String contentType) {
    try {
      long size = Files.size(tempFile);

      File meta = new File(fileName, contentType, size);
      meta = fileRepository.save(meta);

      Path root = Path.of(storageRoot);
      Files.createDirectories(root);

      Path dest = root.resolve(String.valueOf(meta.getId()));
      Files.move(tempFile, dest, StandardCopyOption.REPLACE_EXISTING);

      return new FileResponse(meta.getId(), fileName, contentType, size);
    } catch (IOException e) {
      throw new FileStorageException("로컬 파일 저장 실패: " + tempFile, e);
    }
  }

  // 실패 롤백/정리용: 메타 + 디스크 파일 동시 삭제
  @Transactional
  public void delete(Long id) {
    File meta = fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("파일 메타 정보를 찾을 수 없습니다. id: " + id));
    Path p = Path.of(storageRoot, String.valueOf(id));
    try {
      Files.deleteIfExists(p);
    } catch (IOException e) {
      throw new FileStorageException("디스크 파일 삭제 실패: " + p, e);
    }
    fileRepository.delete(meta);
  }
}
