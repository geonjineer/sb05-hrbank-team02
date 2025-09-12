package com.sprint.project.hrbank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "backups")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // ✅ 반드시 NULL 허용 (SKIPPED 시 파일 없음)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id")
  private File file; // {백업 파일} (성공: csv, 실패: log, 스킵: null)

  @Column(length = 50, nullable = false)
  private String worker; // {작업자}: 요청자 IP or "system"

  @Column(name = "started_at", nullable = false)
  private Instant startedAt; // {시작 시간}

  @Column(name = "ended_at", nullable = false)
  private Instant endedAt;   // {종료 시간} - DDL NOT NULL

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BackupStatus status; // {상태}: IN_PROGRESS, COMPLETED, SKIPPED, FAILED

  // ========================
  // ✅ 도메인 메서드
  // ========================

  public void markInProgress() {
    this.status = BackupStatus.IN_PROGRESS;
    this.endedAt = this.startedAt; // 시작 = 종료 시간 동기화
  }

  public void complete(File file, Instant endedAt) {
    this.file = file;
    this.endedAt = endedAt;
    this.status = BackupStatus.COMPLETED;
  }

  public void fail(File errorFile, Instant endedAt) {
    this.file = errorFile;
    this.endedAt = endedAt;
    this.status = BackupStatus.FAILED;
  }

  public void skip(Instant endedAt) {
    this.file = null; // 스킵 시 파일 없음
    this.endedAt = endedAt;
    this.status = BackupStatus.SKIPPED;
  }
}
