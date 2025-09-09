package com.sprint.project.hrbank.dto.department;

import java.time.LocalDate;

public record DepartmentDto(
    long id,
    String name,
    String description,
    LocalDate establishedDate
) {

}
