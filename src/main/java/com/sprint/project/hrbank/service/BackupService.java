package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.configuration.HrbankProperties;
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

  @Transactional
  public void runBackup(String operator) {
    var zone = ZoneId.of(props.getBackup().getTimezone());
    var now = Instant.now();

    var baseEndedAt = backupRepository
        .findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
        .map(Backup::getEndedAt)
        .orElse(Instant.EPOCH); // Instant로 통일
    boolean need = changeLogRepository.existsByAtAfter(baseEndedAt);

    if (!need) {
      // SKIPPED
      var skipped = Backup.builder()
          .worker(operator)
          .startedAt(now)
          .endedAt(now)
          .status(BackupStatus.SKIPPED)
          .build();
      skipped.skip(now);
      backupRepository.save(skipped);
      // 여기서 예외는 던지지 않음(정상 흐름)
      return;
    }

    // IN_PROGRESS
    var backup = Backup.builder()
        .worker(operator)
        .startedAt(now)
        .build();
    backup.markInProgress();
    backup = backupRepository.save(backup);

    Path tempCsv = null;
    try {
      // ===== STEP.3 CSV 생성 =====
      Path appTmp = Paths.get(System.getProperty("java.io.tmpdir"), "hrbank");
      Files.createDirectories(appTmp);
      tempCsv = Files.createTempFile(appTmp, "hrbank-employees-", ".csv");

      writeEmployeesCsv(tempCsv);

      var ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(zone).format(now);
      var fileName = "employees-" + ts + ".csv";

      var saved = fileService.saveLocal(tempCsv, fileName, "text/csv");

      var fileRef = entityManager.getReference(File.class, saved.id());
      backup.complete(fileRef, Instant.now());
      backupRepository.save(backup);

    } catch (Exception e) {
      try { if (tempCsv != null) Files.deleteIfExists(tempCsv); } catch (IOException ignore) {}

      try {
        Path appTmp = Paths.get(System.getProperty("java.io.tmpdir"), "hrbank");
        Files.createDirectories(appTmp);
        Path tempLog = Files.createTempFile(appTmp, "hrbank-backup-error-", ".log");
        Files.writeString(tempLog, "Backup failed: " + e.getMessage());

        var ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(zone).format(Instant.now());
        var logName = "backup-error-" + ts + ".log";

        var err = fileService.saveLocal(tempLog, logName, "text/plain");
        var logRef = entityManager.getReference(File.class, err.id());
        backup.fail(logRef, Instant.now());
        backupRepository.save(backup);

      } catch (Exception logSaveEx) {
        // 로그 저장마저 실패 → 상태만 FAILED
        backup.fail(null, Instant.now());
        backupRepository.save(backup);
        // 이 단계는 더 이상 던지지 않음(스케줄링 안정성)
      }

      // 원인 자체는 로깅 / 모니터링 대상
      // 필요하면 여기서 BusinessException으로 재던져도 됨(스케줄러 컨텍스트 따라 결정) -> 굳이임
      // throw new BusinessException(ErrorCode.FILE_STORAGE_IO_ERROR, "백업 중 오류", e);
    }
  }

  private void writeEmployeesCsv(Path csv) throws IOException {
    try (BufferedWriter bw = Files.newBufferedWriter(csv)) {
      bw.write("id,employee_number,name,email,department,position,hire_date,status,profile_image_id");
      bw.newLine();

      int page = 0, size = 1000;
      while (true) {
        Page<Employee> slice = employeeRepository.findAll(PageRequest.of(page, size));
        for (Employee e : slice.getContent()) {
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
        if (!slice.hasNext()) break;
        page++;
      }
      bw.flush();
    }
  }

  private String safe(Object o) { return o == null ? "" : String.valueOf(o); }

  private String csvEscape(String s) {
    if (s == null) return "";
    boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
    if (!needQuote) return s;
    return "\"" + s.replace("\"", "\"\"") + "\"";
  }
}