package com.sprint.project.hrbank.dto.employee;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record EmployeeCreateRequest(
    String name,
    String email,
    long departmentId,
    String position,
    LocalDate hireDate,
    String memo
) {

}
