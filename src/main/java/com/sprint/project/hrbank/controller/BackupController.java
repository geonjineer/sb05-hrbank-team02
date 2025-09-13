package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.backup.BackupDto;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.service.BackupReadService;
import com.sprint.project.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;
  private final BackupReadService backupReadService;

  // 수동 백업 실행: {작업자} = 요청자 IP
  @PostMapping
  public ResponseEntity<BackupDto> create(HttpServletRequest req) {
    String ip = req.getRemoteAddr(); // 프록시 처리 app 설정을 신뢰
    backupService.runBackup(ip);
    // 바로 최신 1건 조회하여 반환
    BackupDto latest = backupReadService.findLatestCompletedOrNull();
    return ResponseEntity.ok(latest);
  }

  // 목록(커서) 조회: 쿼리파라미터를 DTO로 바인딩
  @GetMapping
  public ResponseEntity<CursorPageResponse<BackupDto>> list(@ModelAttribute BackupSearchRequest request) {
    return ResponseEntity.ok(backupReadService.search(request));
  }

  // 최근 상태별 1건
  @GetMapping("/latest")
  public ResponseEntity<BackupDto> latest(@RequestParam(defaultValue = "COMPLETED") String status) {
    return ResponseEntity.ok(backupReadService.findLatestByStatusOrNull(status));
  }
}