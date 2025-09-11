package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {

  Optional<Backup> findTopByStatusOrderByStartedAtDesc(BackupStatus status);
}