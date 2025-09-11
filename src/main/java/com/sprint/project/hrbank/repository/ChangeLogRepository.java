package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    ChangeLogQueryRepository {

}
