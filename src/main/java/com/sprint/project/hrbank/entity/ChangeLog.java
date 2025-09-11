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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "change_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
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

  @Column(nullable = false, columnDefinition = "inet")
  private String ipAddress;

  @Column(nullable = false)
  private Instant at;

  @OneToMany(mappedBy = "changLog", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ChangeLogDiff> diffs = new ArrayList<>();

}
