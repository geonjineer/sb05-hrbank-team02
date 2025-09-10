package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

public record EmployeeCountSearchRequest(
    EmployeeStatus employeeStatus,
    LocalDate fromDate,
    LocalDate toDate
) {

}
