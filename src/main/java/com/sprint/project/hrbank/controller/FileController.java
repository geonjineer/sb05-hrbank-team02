package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.common.FileResponse;
import com.sprint.project.hrbank.service.FileService;

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
public class FileController {

  private final FileService fileService;

  // 파일 다운로드 API 공개 엔드 포인트
  // GET /api/files/{id}/download

  // meta(id, fileName, contentType, size)를 조회해 응답 헤더에 반영한다.
  // 실제 파일은 storage/{id}에서 읽어 반환한다.
  // 예외는 exception.GlobalExceptionHandler에서 처리한다.

  @GetMapping("/{id}/download")
  public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {

    // meta 조회: fileName/contentType 헤더에 사용한다.
    FileResponse meta = fileService.getMeta(id);

    //실제 파일 리소스
    FileSystemResource resource = fileService.download(id);

    //fileName 한글 and 특수문자 안전 처리
    String fileName = StringUtils.hasText(meta.fileName()) ? meta.fileName() : String.valueOf(id);
    String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
    String contentDisposition =
        "attachment; filename=\"" + fileName.replace("\"", "") + "\"; filename*=UTF-8''" + encoded;

    //Content Type meta에 없거나 빈칸이면 octet-stream
    String contentType = StringUtils.hasText(meta.contentType()) ? meta.contentType()
        : MediaType.APPLICATION_OCTET_STREAM_VALUE;

    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentType(MediaType.parseMediaType(contentType)).body(resource);
  }

}

