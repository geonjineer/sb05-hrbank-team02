package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeCountSearchRequest(
    EmployeeStatus employeeStatus,
    @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
    @DateTimeFormat(iso = ISO.DATE) LocalDate toDate
) {

}
