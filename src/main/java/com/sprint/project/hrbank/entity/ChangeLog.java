package com.sprint.project.hrbank.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "change_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, nullable = false)
  private ChangeLogType type;

  @Column(nullable = false)
  private String employeeNumber;

  @Column(length = 500)
  private String memo;

  @Column(length = 50, nullable = false)
  private String ipAddress;

  @Column(nullable = false)
  private Instant at;

  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<ChangeLogDiff> diffs = new ArrayList<>();

  public void addDiff(ChangeLogDiff diff) {
    diffs.add(diff);
    diff.setChangeLog(this);
  }

  public ChangeLog(ChangeLogType type, String employeeNumber, String memo, String ipAddress,
      Instant at) {
    this.type = type;
    this.employeeNumber = employeeNumber;
    this.memo = memo;
    this.ipAddress = ipAddress;
    this.at = at;
  }
}
