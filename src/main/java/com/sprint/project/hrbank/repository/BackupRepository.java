package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface BackupRepository extends JpaRepository<Backup, Long>, BackupQueryRepository {

  // 최근 완료 이력 1건
  @NonNull
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(@NonNull BackupStatus status);

  // (간단 버전) 기본 페이지
  @NonNull
  Page<Backup> findAll(@NonNull Pageable pageable);

  // 파생 메서드로 간단 필터
  @NonNull
  Page<Backup> findByWorkerContainingIgnoreCaseAndStatusAndStartedAtBetween(
      @NonNull String workerKeyword,
      @NonNull BackupStatus status,
      @NonNull OffsetDateTime from,
      @NonNull OffsetDateTime to,
      @NonNull Pageable pageable
  );
}