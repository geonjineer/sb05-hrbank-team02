package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

// 수정할 데이터만 담는 DTO
public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    String memo
) {

}
