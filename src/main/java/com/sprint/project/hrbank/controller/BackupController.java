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

  /**
   * 수동 백업 실행: {작업자} = 요청자 IP
   * 실행 직후 "최신 완료 백업"을 즉시 조회하여 반환 (없으면 예외)
   */
  @PostMapping
  public ResponseEntity<BackupDto> create(HttpServletRequest req) {
    String ip = req.getRemoteAddr(); // 프록시 설정은 application.yml의 server.*에 따름
    backupService.runBackup(ip);
    return ResponseEntity.ok(backupReadService.findLatestCompleted());
  }

  /**
   * 백업 목록(커서) 조회
   * 쿼리 파라미터를 DTO로 자동 바인딩(@ModelAttribute)
   */
  @GetMapping
  public ResponseEntity<CursorPageResponse<BackupDto>> list(@ModelAttribute BackupSearchRequest request) {
    return ResponseEntity.ok(backupReadService.search(request));
  }

  /**
   * 최근 상태별 1건 조회
   * 상태명 유효성/데이터 없음은 BusinessException으로 처리
   */
  @GetMapping("/latest")
  public ResponseEntity<BackupDto> latest(@RequestParam(defaultValue = "COMPLETED") String status) {
    return ResponseEntity.ok(backupReadService.findLatestByStatusOrThrow(status));
  }
}