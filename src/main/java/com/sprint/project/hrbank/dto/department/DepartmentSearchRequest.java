package com.sprint.project.hrbank.dto.department;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.*;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record DepartmentSearchRequest(
    String keyword,
    String name,
    @DateTimeFormat(iso = ISO.DATE) LocalDate establishedDate,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField, // establishedDate || name
    String sortDirection // asc || desc
) {

  public static DepartmentSearchRequest of(DepartmentSearchRequest r) {
    Integer size = clampSize(r.size, 10, 1, 100);

    Set<String> allowedFields = Set.of("establishedDate", "name");
    String sortField = normalizeString(r.sortField, allowedFields, "name");
    String sortDirection = normalizeSortDirection(r.sortDirection, "asc");

    return new DepartmentSearchRequest(
        r.keyword(),
        r.name(),
        r.establishedDate(),
        r.idAfter(),
        r.cursor(),
        size,
        sortField,
        sortDirection
    );
  }
}
