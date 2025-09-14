package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.ChangeLog;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>,
    ChangeLogQueryRepository {

  @Query("select count (c) from ChangeLog c where c.at > :base")
  Long countChangeAfter(OffsetDateTime base);

  boolean existsByAtAfter(@NonNull Instant time);

  Long countChangeLogByAtBetween(Instant atBefore, Instant atAfter);

}

