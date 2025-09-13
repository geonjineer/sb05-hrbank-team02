package com.sprint.project.hrbank.dto.department;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.clampSize;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeSortDirection;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeString;

import java.util.Set;

public record DepartmentSearchRequest(
    String nameOrDescription,
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
        r.nameOrDescription(),
        r.idAfter(),
        r.cursor(),
        size,
        sortField,
        sortDirection
    );
  }
}
