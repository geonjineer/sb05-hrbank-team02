package com.sprint.project.hrbank.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeCreateRequest(
    @NotBlank(message = "NAME_REQUIRED")
    @Size(max = 100, message = "NAME_TOO_LONG")
    String name,

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    @Size(max = 100, message = "EMAIL_TOO_LONG")
    String email,

    @Min(value = 1, message = "DEPARTMENT_ID_MIN")
    long departmentId,

    @Size(max = 50, message = "POSITION_TOO_LONG")
    String position,

    @NotNull(message = "HIRE_DATE_REQUIRED")
    @PastOrPresent(message = "HIRE_DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDate,

    @Size(max = 500, message = "MEMO_TOO_LONG")
    String memo
) {

}
