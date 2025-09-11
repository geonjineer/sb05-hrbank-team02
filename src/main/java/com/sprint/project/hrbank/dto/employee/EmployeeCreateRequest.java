package com.sprint.project.hrbank.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record EmployeeCreateRequest(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 100, message = "이름은 최대 100자까지 가능합니다.")
    String name,

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 최대 100자까지 가능합니다.")
    String email,

    @Min(value = 1, message = "부서 ID는 1 이상의 값이어야 합니다.")
    long departmentId,

    @Size(max = 50, message = "직함은 최대 50자까지 가능합니다.")
    String position,

    @NotNull(message = "입사일은 필수입니다.")
    @PastOrPresent(message = "입사일은 오늘 또는 과거여야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // 'yyyy-MM-dd' 로 바인딩
    LocalDate hireDate,

    @Size(max = 500, message = "메모는 최대 500자까지 가능합니다.")
    String memo
) {

}
