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

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  //FK files.id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "file_id", nullable = false)
  private File fIle;

  @Column(length = 50, nullable = false)
  private String worker;

  @Column(name = "started_At", nullable = false)
  private OffsetDateTime startedAt;

  @Column(name = "ended_At", nullable = false)
  private OffsetDateTime endedAt;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BackupStatus status;


  //생성자(endedAt DDL이 NOT NULL이라 초기값을 startedAt으로 셋팅)
  public Backup(File fIle, String worker, OffsetDateTime startedAt, OffsetDateTime endedAt, BackupStatus status) {
    this.fIle = fIle;
    this.worker = worker;
    this.startedAt = startedAt;
    this.endedAt = startedAt;
    this.status = status;
  }

  public void complete(File fIle, OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    this.status = BackupStatus.COMPLETED;
    this.fIle = fIle;
  }

  public void fail(File logFIle, OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    this.status = BackupStatus.FAILED;
    this.fIle = logFIle;
  }

  public void skip(OffsetDateTime endedAt) {
    this.endedAt = endedAt;
    this.status = BackupStatus.SKIPPED;
  }

}
