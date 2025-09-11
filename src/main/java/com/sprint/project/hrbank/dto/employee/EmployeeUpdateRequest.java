package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

// 수정할 데이터만 담는 DTO
public record EmployeeUpdateRequest(
    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    String name,

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 최대 100자까지 가능합니다.")
    String email,

    @Min(value = 1, message = "부서 ID는 1 이상의 값이어야 합니다.")
    Long departmentId,

    @Size(max = 50, message = "직함은 최대 50자까지 가능합니다.")
    String position,

    @PastOrPresent(message = "입사일은 오늘 또는 과거여야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate hireDate,

    EmployeeStatus status,

    @Size(max = 500, message = "메모는 최대 500자까지 가능합니다.")
    String memo
) {

}
