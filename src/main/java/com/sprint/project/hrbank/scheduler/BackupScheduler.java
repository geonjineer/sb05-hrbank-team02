package com.sprint.project.hrbank.scheduler;

import com.sprint.project.hrbank.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;

  // cron / timezone 은 application.yml 의 hrbank.backup.* 사용
  @Scheduled(cron = "${hrbank.backup.cron}", zone = "${hrbank.backup.timezone:UTC}")
  public void runHourlyBackup() {
    log.info("🔄 Scheduled backup started (system)");
    backupService.runBackup("system");
    log.info("✅ Scheduled backup finished");
  }
}
