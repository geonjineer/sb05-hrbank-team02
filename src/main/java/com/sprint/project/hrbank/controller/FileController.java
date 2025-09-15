package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "파일 관리", description = "파일 관리 API")
public class FileController {

  private final FileService fileService;

  // 다운로드 (공개 스펙)
  @Operation(summary = "파일 다운로드")
  @GetMapping("/{id}/download")
  public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {
    FileResponse meta = fileService.getMeta(id);
    FileSystemResource resource = fileService.download(id);

    // 서비스에서 이미 안전한 파일명을 보장하지만,
    // 혹시라도 과거 데이터가 있을 수 있어 계층적 방어(Defense in Depth) 원칙 적용
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