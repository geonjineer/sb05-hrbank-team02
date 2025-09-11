package com.sprint.project.hrbank.repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import com.sprint.project.hrbank.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    ChangeLogQueryRepository {
  @Query("select count (c) from ChangeLog c where c.at > :base")
  Long countChangeAfter(OffsetDateTime base);

  Long countChangeLogByAtBetween(Instant atBefore, Instant atAfter);

}
