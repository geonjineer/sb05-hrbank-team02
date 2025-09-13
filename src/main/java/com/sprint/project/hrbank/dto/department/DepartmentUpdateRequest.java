package com.sprint.project.hrbank.dto.department;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record DepartmentUpdateRequest(
    @NotBlank(message = "DEPARTMENT_NAME_REQUIRED")
    @Max(value = 100, message = "DEPARTMENT_NAME_TOO_LONG")
    String name,

    @Max(value = 1000, message = "DEPARTMENT_DESCRIPTION_TOO_LONG")
    String description,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate establishedDate
) {

}
