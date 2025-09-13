package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

// 수정할 데이터만 담는 DTO
public record EmployeeUpdateRequest(
    @Max(value = 100, message = "EMPLOYEE_NAME_REQUIRED")
    String name,

    @Email(message = "EMAIL_INVALID")
    @Max(value = 100, message = "EMAIL_TOO_LONG")
    String email,

    @Min(value = 1, message = "ENTITY_ID_MIN")
    Long departmentId,

    @Max(value = 50, message = "POSITION_TOO_LONG")
    String position,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDate,

    EmployeeStatus status,

    @Max(value = 500, message = "MEMO_TOO_LONG")
    String memo
) {

}
