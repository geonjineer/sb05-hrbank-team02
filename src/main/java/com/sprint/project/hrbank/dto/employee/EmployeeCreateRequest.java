package com.sprint.project.hrbank.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeCreateRequest(
    @NotBlank(message = "EMPLOYEE_NAME_REQUIRED")
    @Max(value = 100, message = "EMPLOYEE_NAME_TOO_LONG")
    String name,

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    @Max(value = 100, message = "EMAIL_TOO_LONG")
    String email,

    @NotNull(message = "DEPARTMENT_ID_REQUIRED")
    @Positive(message = "ENTITY_ID_MIN")
    Long departmentId,

    @Max(value = 50, message = "POSITION_TOO_LONG")
    String position,

    @NotNull(message = "DATE_REQUIRED")
    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDate,

    @Max(value = 500, message = "MEMO_TOO_LONG")
    String memo
) {

}
