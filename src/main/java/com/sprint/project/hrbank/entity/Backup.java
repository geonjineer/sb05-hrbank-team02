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
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  // 실패 케이스에서 파일이 없을 수도 있으니 NULL 허용
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id")
  private File file; // 성공: csv, 실패: log, 스킵/진행중: null 가능

  @Column(length = 50, nullable = false)
  private String worker; // 요청자 IP or "system"

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "ended_at", nullable = false)
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BackupStatus status;

  // 도메인 메서드
  public void markInProgress() {
    this.status = BackupStatus.IN_PROGRESS;
    this.endedAt = this.startedAt; // NOT NULL 제약 충족
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
    this.endedAt = endedAt;
    this.status = BackupStatus.SKIPPED;
  }
}