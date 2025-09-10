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

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileRepository fileRepository;

  @Value("${hrbank.storage.root:./storage}")
  private String storageRoot;

  @Transactional
  public FileResponse upload(MultipartFile multifile, String fileName, String contentType) {
    final long size = multifile.getSize();

    File meta = new File(fileName, contentType, size);
    meta = fileRepository.save(meta);

    final Path root = Path.of(storageRoot);
    try {
      Files.createDirectories(root);
      final Path dest = root.resolve(String.valueOf(meta.getId()));
      try (InputStream inputStream = multifile.getInputStream()) {
        Files.copy(inputStream, dest, StandardCopyOption.REPLACE_EXISTING);
      }
      return new FileResponse(meta.getId(), fileName, contentType, size);
    } catch (IOException e) {
      fileRepository.deleteById(meta.getId());
      throw new FileStorageException("디스크 저장에 실패했습니다. id: " + meta.getId(), e);
    }
  }

  @Transactional(readOnly = true)
  public FileResponse getMeta(Long id) {
    File meta = fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("파일 메타 정보를 찾을 수 없습니다. id: " + id));
    return new FileResponse(meta.getId(), meta.getFileName(), meta.getContentType(),
        meta.getSize());
  }

  @Transactional(readOnly = true)
  public FileSystemResource download(Long id) {
    fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("파일 메타 정보를 찾을 수 없습니다. id: " + id));

    Path filePath = Path.of(storageRoot, String.valueOf(id));
    if (!Files.exists(filePath)) {
      throw new FileStorageException("디스크에서 파일을 찾을 수 없습니다. path: " + filePath);
    }
    return new FileSystemResource(filePath);
  }
}
