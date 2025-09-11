package com.sprint.project.hrbank.dto.changeLog;

import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record ChangeLogCountSearchRequest(
    @DateTimeFormat(iso = ISO.DATE) Instant fromDate,
    @DateTimeFormat(iso = ISO.DATE) Instant toDate
) {
  // 기본값 설정 및 날짜 유효성 체크
  public ChangeLogCountSearchRequest {
    if (toDate == null) {
      toDate = Instant.now();
    }
    if (fromDate == null) {
      fromDate = toDate.minusSeconds(7 * 24 * 60 * 60);
    }
    if (toDate.isBefore(fromDate)) {
      throw new IllegalArgumentException("toDate must be before fromDate");
    }
  }
}
