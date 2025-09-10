package com.sprint.project.hrbank.dto.department;

import java.time.LocalDate;

public record DepartmentUpdateRequest(
    String name,
    String description,
    LocalDate establishedDate
) {

}
