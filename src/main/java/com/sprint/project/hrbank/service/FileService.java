package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.dto.common.FileResponse;
import com.sprint.project.hrbank.repository.FileRepository;

import java.io.IOException;
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

  // application.yml 에서 hrbank.storage.root 값을 읽어옴
  // 설정이 없으면 기본값 "./storage" 사용
  @Value("${hrbank.storage.root:./storage}")
  private String storageRoot;

  //파일 업로드 서비스
  @Transactional
  public FileResponse upload(MultipartFile multifile) throws IOException {
    // 원본 이름, 타입, 크기 추출
    final String rawName = multifile.getOriginalFilename();
    final String rawType = multifile.getContentType();
    final long size = multifile.getSize(); // size는 primitive long → null 불가

    // Null/빈값 방어
    // isBlank()는 빈 문자열("") + 공백("   ") 모두 true → fileName이 공백만 있어도 "이름없음"으로 대체됨 isEmpty()보다 더 안전
    final String fileName =
        (rawName == null || rawName.isBlank()) ? "이름없음" : rawName;

    // MIME 타입이 null/빈값이면 "application/octet-stream" (일반적인 바이너리 타입)으로 대체
    final String contentType =
        (rawType == null || rawType.isBlank()) ? "application/octet-stream" : rawType;

    // 1. 메타데이터 DB 저장 (id 자동 생성)
    File meta = new File(null, fileName, contentType, size);
    meta = fileRepository.save(meta);

    // 2. 실제 파일 저장 경로: storage/{id}
    final Path root = Path.of(storageRoot);
    Files.createDirectories(root); // 디렉토리 없으면 생성

    final Path dest = root.resolve(String.valueOf(meta.getId()));

    try (InputStream inputStream = multifile.getInputStream()) {
      // 파일 저장 (기존 파일 있으면 덮어쓰기)
      Files.copy(inputStream, dest, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      // 디스크 저장 실패 → 메타데이터 롤백 (DB/디스크 불일치 방지)
      fileRepository.deleteById(meta.getId());
      throw e; // 예외 다시 던져서 호출자에게 알림
    }

    // 업로드 결과 반환 DTO
    return new FileResponse(meta.getId(), fileName, contentType, size);
  }

  //파일 메타 정보 조회
  //DB에서만 확인 → 실제 파일은 확인하지 않음
  @Transactional(readOnly = true)
  public FileResponse getMeta(Long id) {
    File meta = fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

    return new FileResponse(meta.getId(), meta.getFileName(), meta.getContentType(),
        meta.getSize());
  }

  //파일 다운로드
  @Transactional(readOnly = true)
  public FileSystemResource download(Long id) {
    // 1. DB 메타 존재 확인
    fileRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

    // 2. 디스크에 실제 파일 존재 확인
    Path filePath = Path.of(storageRoot, String.valueOf(id));
    if (!Files.exists(filePath)) {
      throw new IllegalArgumentException("디스크에서 파일을 찾을 수 없습니다. path=" + filePath);
    }
    // 두 레이어(DB/디스크)를 각각 확인하여 안정성 확보
    // 파일 리소스를 Spring MVC에서 바로 ResponseEntity로 반환 가능
    return new FileSystemResource(filePath);
  }
}