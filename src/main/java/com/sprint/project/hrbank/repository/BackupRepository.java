package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface BackupRepository extends JpaRepository<Backup, Long>, BackupQueryRepository {

  @NonNull
  Optional<Backup> findTopByStatusOrderByEndedAtDesc(@NonNull BackupStatus status);
}
