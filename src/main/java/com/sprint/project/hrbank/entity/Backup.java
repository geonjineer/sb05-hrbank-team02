package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@AllArgsConstructor
@NoArgsConstructor
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private long id;

  @Column(updatable = false, nullable = false)
  private String worker;
  private Instant startedAt;
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  private BackupStatus status;

}
