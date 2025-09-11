package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeSearchRequest(
    String nameOrEmail,
    String employeeNumber,
    String departmentName,
    String position,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDateFrom,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDateTo,

    EmployeeStatus status,
    Long idAfter,
    String cursor,

    @Min(value = 1,  message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 최대 100까지 가능합니다.")
    Integer size,

    String sortField,     // name | employeeNumber | hireDate
    String sortDirection  // asc | desc
) {

}
