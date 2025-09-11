package com.sprint.project.hrbank.dto.department;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record DepartmentSearchRequest(
    String keyword,
    String name,
    @DateTimeFormat(iso = ISO.DATE) LocalDate establishedDate,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {

}
