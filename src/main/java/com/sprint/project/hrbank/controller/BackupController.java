package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.backup.BackupItemDto;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.entity.BackupStatus;
import com.sprint.project.hrbank.service.BackupReadService;
import com.sprint.project.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;       // 실행
  private final BackupReadService backupReadService; // 조회

  // POST /api/backups : 데이터 백업 생성 (요청자 IP)
  @PostMapping
  public ResponseEntity<BackupItemDto> create(HttpServletRequest req) {
    String ip = extractClientIp(req);
    backupService.runBackup(ip);

    // 방금 실행한 결과를 latest(COMPLETED 기준)로 반환하는 간단 전략
    BackupItemDto latest = backupReadService.latest(BackupStatus.COMPLETED);
    return ResponseEntity.ok(latest);
  }

  // GET /api/backups/latest : 최근 백업 1건 조회 (상태 기본 COMPLETED)
  @GetMapping("/latest")
  public ResponseEntity<BackupItemDto> latest(
      @RequestParam(name = "status", required = false, defaultValue = "COMPLETED")
      BackupStatus status
  ) {
    return ResponseEntity.ok(backupReadService.latest(status));
  }

  // GET /api/backups : 목록(커서 페이징)
  @GetMapping
  public ResponseEntity<CursorPageResponse<BackupItemDto>> list(
      @RequestParam(required = false) String worker,
      @RequestParam(required = false) BackupStatus status,
      @RequestParam(required = false, name = "startedAtFrom")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startedAtFrom,
      @RequestParam(required = false, name = "startedAtTo")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startedAtTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "startedAt") String sortField,
      @RequestParam(defaultValue = "DESC") String sortDirection
  ) {
    // 파라미터를 DTO로 묶어서 서비스로 전달
    BackupSearchRequest req = new BackupSearchRequest(
        worker, status, startedAtFrom, startedAtTo,
        idAfter, cursor, size, sortField, sortDirection
    );
    return ResponseEntity.ok(backupReadService.search(req));
  }

  private String extractClientIp(HttpServletRequest req) {
    String xff = req.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
      String first = xff.split(",")[0].trim();
      if (!first.isBlank()) return first;
    }
    String ip = req.getRemoteAddr();
    return (ip == null || ip.isBlank()) ? "unknown" : ip;
  }
}