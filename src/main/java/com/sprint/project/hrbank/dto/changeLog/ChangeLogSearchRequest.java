package com.sprint.project.hrbank.dto.changeLog;

import com.sprint.project.hrbank.entity.ChangeLogType;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record ChangeLogSearchRequest(
    String employeeNumber,
    ChangeLogType type,
    String memo,
    String ipAddress,
    @DateTimeFormat(iso = ISO.DATE) Instant atFrom,
    @DateTimeFormat(iso = ISO.DATE) Instant atTo,
    Long idAfter,
    String cursor,

    @Size(min = 1, max = 100, message = "페이지 크기는 1 이상 100 이하여야 합니다")
    Integer size,
    String sortField,
    String sortDirection
) {

  public ChangeLogSearchRequest {
    if (size == null) {
      size = 10;
    }
    if (!sortField.equals("ipAddress") && !sortField.equals("at")) {
      sortField = "at";
    }
    if (!sortDirection.equals("asc") && !sortDirection.equals("desc")) {
      sortDirection = "desc";
    }
    if (atTo != null && atFrom != null && atTo.isBefore(atFrom)) {
      throw new IllegalArgumentException("atTo는 atFrom 이전일 수 없습니다.");
    }
    if (atTo != null && atTo.isBefore(Instant.now())) {
      throw new IllegalArgumentException("atTo는 현재 시간 이후일 수 없습니다.");
    }

  }

}
