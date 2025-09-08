package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.dto.common.FileResponse;
import com.sprint.project.hrbank.repository.FileRepository;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  @Value("${hrbank.storage.root:./storage}")
  private String storageRoot;

  // 파일 업로드
  @Transactional
  public FileResponse upload(MultipartFile multifile) throws Exception {
    final String fileName = multifile.getOriginalFilename();
    final String contentType = multifile.getContentType();
    final long size = multifile.getSize();

    // 1. meta 저장 후 id 발급
    File meta = new File(null, fileName, contentType, size);
    meta = fileRepository.save(meta);

    // 2. 실제 파일을 storage/{id} 경로에 저장
    final Path root = Path.of(storageRoot);
    Files.createDirectories(root);

    final Path dest = root.resolve(String.valueOf(meta.getId()));
    try (InputStream inputStream = multifile.getInputStream()) {
      Files.copy(inputStream, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    return new FileResponse(meta.getId(), fileName, contentType, size);
  }

  // 파일 meta 조회
  @Transactional(readOnly = true)
  public FileResponse getMeta(Long id) {
    File meta = fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

    return new FileResponse(meta.getId(), meta.getFileName(), meta.getContentType(),
        meta.getSize());
  }

  // 파일 다운로드 리소스
  @Transactional(readOnly = true)
  public FileSystemResource download(Long id) {
    // meta 존재 확인
    fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

    Path filePath = Path.of(storageRoot, String.valueOf(id));
    if (!Files.exists(filePath)) {
      throw new IllegalArgumentException("디스크에서 파일을 찾을 수 없습니다. path=" + filePath);
    }

    return new FileSystemResource(filePath);
  }
}
