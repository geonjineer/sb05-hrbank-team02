package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record EmployeeDto(
    long id,
    String name,
    String email,
    String employeeNumber,
    long departmentId,
    String departmentName,
    String position,
    LocalDate hireDate,
    EmployeeStatus status
) {

}
