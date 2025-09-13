package com.sprint.project.hrbank.dto.changeLog;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.clampSize;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeSortDirection;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeString;

import com.sprint.project.hrbank.entity.ChangeLogType;
import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Instant;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@DateRange(from = "atFrom", to = "atTo")
public record ChangeLogSearchRequest(
    String employeeNumber,
    ChangeLogType type,
    String memo,
    String ipAddress,

    @DateTimeFormat(iso = ISO.DATE_TIME)
    Instant atFrom,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE_TIME)
    Instant atTo,

    Long idAfter,
    String cursor,
    Integer size,
    String sortField, // at || ipAddress
    String sortDirection // desc || asc
) {

  public static ChangeLogSearchRequest of(ChangeLogSearchRequest r) {
    Integer size = clampSize(r.size, 10, 1, 100);

    Set<String> allowedFields = Set.of("at", "ipAddress");
    String sortField = normalizeString(r.sortField, allowedFields, "at");
    String sortDirection = normalizeSortDirection(r.sortDirection, "desc");

    return new ChangeLogSearchRequest(
        r.employeeNumber(),
        r.type(),
        r.memo(),
        r.ipAddress(),
        r.atFrom(),
        r.atTo(),
        r.idAfter(),
        r.cursor(),
        size,
        sortField,
        sortDirection
    );
  }
}
