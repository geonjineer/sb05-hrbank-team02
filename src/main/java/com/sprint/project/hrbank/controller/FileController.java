package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.service.FileService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  // 파일명 정규화(+ null/blank 방어)
  private String normalizeFileName(String fileName) {
    String normalName = (fileName == null || fileName.isBlank()) ? "이름없음" : fileName.trim();
    normalName = normalName.chars()
        .filter(c -> c >= 32 && c != 127)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    normalName = normalName.replaceAll("[\\\\/:*?\"<>|]", "_");
    normalName = normalName.replaceAll("\\s+", " ");
    if (normalName.equals(".") || normalName.equals("..")) {
      normalName = "file";
    }
    if (normalName.length() > 255) {
      normalName = normalName.substring(0, 255);
    }
    return normalName;
  }

  // Content-Type 정규화(+ null/blank 방어)
  private String normalizeContentType(String contentType) {
    if (contentType == null || contentType.isBlank()) {
      return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
    String t = contentType.trim().toLowerCase();
    return t.matches("^[\\w!#$&^_.+-]+/[\\w!#$&^_.+-]+$") ? t
        : MediaType.APPLICATION_OCTET_STREAM_VALUE;
  }

  // (팀내 사용) 업로드 – 스펙 공개 엔드포인트가 필요 없으면 이 메서드는 삭제해도 됨
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileResponse> upload(@RequestPart("file") MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    final String safeFileName = normalizeFileName(file.getOriginalFilename());
    final String safeContentType = normalizeContentType(file.getContentType());

    FileResponse saved = fileService.upload(file, safeFileName, safeContentType);
    return ResponseEntity.ok(saved);
  }

  // 다운로드 (공개 스펙)
  @GetMapping("/{id}/download")
  public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {
    FileResponse meta = fileService.getMeta(id);
    FileSystemResource resource = fileService.download(id);

    String fileName = StringUtils.hasText(meta.fileName()) ? meta.fileName() : String.valueOf(id);
    String asciiFileName = fileName.replace("\"", "");
    String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
    String contentDisposition =
        "attachment; filename=\"" + asciiFileName + "\"; filename*=UTF-8''" + encoded;

    String contentType = StringUtils.hasText(meta.contentType())
        ? meta.contentType()
        : MediaType.APPLICATION_OCTET_STREAM_VALUE;

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
  }
}
