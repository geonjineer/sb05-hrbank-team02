package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeSearchRequest(
    String nameOrEmail,
    String employeeNumber,
    String departmentName,
    String position,
    @DateTimeFormat(iso = ISO.DATE) LocalDate hireDateFrom,
    @DateTimeFormat(iso = ISO.DATE) LocalDate hireDateTo,
    EmployeeStatus status,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,    // name | employeeNumber | hireDate
    String sortDirection // asc | desc
) {

}
