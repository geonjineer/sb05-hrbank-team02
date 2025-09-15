package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.configuration.HrbankProperties;
import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.repository.BackupRepository;
import com.sprint.project.hrbank.repository.ChangeLogRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

  private final EmployeeRepository employeeRepository;
  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;
  private final FileService fileService;
  private final HrbankProperties props;
  private final EntityManager entityManager;

  /**
   * 스케줄러/수동 공용 진입점
   *
   * @param operator 요청자 IP 또는 "system"
   */
  @Transactional
  public void runBackup(String operator) {
    // ===== 공통 시간/포맷 준비 =====
    ZoneId zone = ZoneId.of(props.getBackup().getTimezone());
    Instant now = Instant.now();
    DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(zone);

    // ===== STEP.1 필요 여부 판단 =====
    Instant base = backupRepository
        .findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
        .map(Backup::getEndedAt)
        .orElse(Instant.EPOCH); // 과거 매우 오래전(1970-01-01T00:00:00Z)

    boolean needBackup = changeLogRepository.existsByAtAfter(base);
    if (!needBackup) {
      // --- SKIPPED 경로 ---
      Backup skipped = Backup.builder()
          .worker(operator)
          .startedAt(now)
          .endedAt(now) // DDL: NOT NULL
          .status(BackupStatus.SKIPPED)
          .build();
      // 도메인 메서드 호출(명시적 전이)
      skipped.skip(now);
      backupRepository.save(skipped);

      log.info("Backup skipped: no changes since {}", base);
      return;
    }

    // ===== STEP.2 IN_PROGRESS 등록 =====
    Backup backup = Backup.builder()
        .worker(operator)
        .startedAt(now)
        .build();
    backup.markInProgress(); // endedAt=startedAt, status=IN_PROGRESS
    backup = backupRepository.save(backup);

    Path tempCsv = null;
    try {
      // ===== STEP.3 CSV 생성 =====
      Path appTmp = Paths.get(System.getProperty("java.io.tmpdir"), "hrbank");
      Files.createDirectories(appTmp);
      tempCsv = Files.createTempFile(appTmp, "hrbank-employees-", ".csv");
      writeEmployeesCsv(tempCsv);

      // 파일명: employees-YYYYMMddHHmmss.csv (표시는 타임존, 저장값은 Instant)
      String fileName = "employees-" + TS.format(now) + ".csv";

      // CSV를 로컬 스토리지로 이동 + 메타 등록
      FileResponse saved = fileService.saveLocal(tempCsv, fileName, "text/csv");

      // ===== STEP.4-1 성공 처리 =====
      File fileRef = entityManager.getReference(File.class, saved.id());
      backup.complete(fileRef, Instant.now());
      backupRepository.save(backup);
      log.info("Backup completed: fileId={}, name={}", saved.id(), fileName);

    } catch (Exception e) {
      log.error("Backup failed", e);
      // 실패 시 임시 파일 삭제 시도
      try {
        if (tempCsv != null) {
          Files.deleteIfExists(tempCsv);
        }
      } catch (IOException ignore) {
      }

      // 에러 로그 저장 (STEP.4-2)
      try {
        Path appTmp = Paths.get(System.getProperty("java.io.tmpdir"), "hrbank");
        Files.createDirectories(appTmp);
        Path tempLog = Files.createTempFile(appTmp, "hrbank-backup-error-", ".log");
        Files.writeString(tempLog, "Backup failed: " + e.getMessage());

        String logName = "backup-error-" + TS.format(Instant.now()) + ".log";
        FileResponse err = fileService.saveLocal(tempLog, logName, "text/plain");

        File logRef = entityManager.getReference(File.class, err.id());
        backup.fail(logRef, Instant.now());
        backupRepository.save(backup);

      } catch (Exception logSaveEx) {
        // 에러 로그 저장도 실패 → 최소한 FAILED + 종료시각은 기록
        backup.fail(null, Instant.now());
        backupRepository.save(backup);
      }
    }
  }

  /**
   * 직원 전체를 CSV로 출력(페이지 처리)
   */
  private void writeEmployeesCsv(Path csv) throws IOException {
    try (BufferedWriter bw = Files.newBufferedWriter(csv)) {
      bw.write(
          "id,employee_number,name,email,department,position,hire_date,status,profile_image_id");
      bw.newLine();

      int page = 0;
      int size = 1000;
      while (true) {
        Page<Employee> slice = employeeRepository.findAll(PageRequest.of(page, size));
        List<Employee> rows = slice.getContent();
        for (Employee e : rows) {
          bw.write(String.join(",",
              safe(e.getId()),
              safe(e.getEmployeeNumber()),
              csvEscape(e.getName()),
              csvEscape(e.getEmail()),
              csvEscape(e.getDepartment() != null ? e.getDepartment().getName() : ""),
              csvEscape(e.getPosition()),
              safe(e.getHireDate()),
              safe(e.getStatus()),
              safe(e.getProfileImage() != null ? e.getProfileImage().getId() : null)
          ));
          bw.newLine();
        }
        if (!slice.hasNext()) {
          break;
        }
        page++;
      }
      bw.flush();
    }
  }

  private String safe(Object o) {
    return o == null ? "" : String.valueOf(o);
  }

  private String csvEscape(String s) {
    if (s == null) {
      return "";
    }
    boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
    if (!needQuote) {
      return s;
    }
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }
}
