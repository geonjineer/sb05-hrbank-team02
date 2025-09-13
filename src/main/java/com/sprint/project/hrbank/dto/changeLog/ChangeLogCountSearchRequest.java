package com.sprint.project.hrbank.dto.changeLog;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.defaultInstant;

import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@DateRange(from = "hireDate", to = "toDate")
public record ChangeLogCountSearchRequest(

    @DateTimeFormat(iso = ISO.DATE_TIME)
    Instant fromDate,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    Instant toDate
) {

  public static ChangeLogCountSearchRequest of(ChangeLogCountSearchRequest r) {
    Instant toDate = defaultInstant(r.toDate(), Instant.now());
    Instant fromDate = defaultInstant(r.fromDate(), toDate.minusSeconds(7 * 24 * 60 * 60));

    return new ChangeLogCountSearchRequest(fromDate, toDate);
  }
}
