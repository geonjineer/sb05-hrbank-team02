package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

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

  // ✅ 반드시 NULL 허용
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = true) // 또는 생략(default nullable)
  private File file; // {백업 파일} (성공: csv, 실패: log)

  @Column(length = 50, nullable = false)
  private String worker; // {작업자}: 요청자 IP or "system"

  @Column(name = "started_at", nullable = false)
  private OffsetDateTime startedAt; // {시작 시간}

  @Column(name = "ended_at", nullable = false)
  private OffsetDateTime endedAt;   // {종료 시간} - DDL NOT NULL

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BackupStatus status; // {상태}: IN_PROGRESS, COMPLETED, SKIPPED, FAILED

  // 도메인 메서드
  public void markInProgress() {
    this.status = BackupStatus.IN_PROGRESS;
    // DDL 때문에 시작 시 endedAt = startedAt 로 일단 세팅
    this.endedAt = this.startedAt;
  }
  public void complete(File file, OffsetDateTime endedAt) {
    this.file = file;
    this.endedAt = endedAt;
    this.status = BackupStatus.COMPLETED;
  }
  public void fail(File errorFile, OffsetDateTime endedAt) {
    this.file = errorFile;
    this.endedAt = endedAt;
    this.status = BackupStatus.FAILED;
  }
  public void skip(OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    this.status = BackupStatus.SKIPPED;
  }
}