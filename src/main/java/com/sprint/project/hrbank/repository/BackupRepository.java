package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<File, Long> {

}
