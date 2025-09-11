package com.sprint.project.hrbank.dto.employee;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record EmployeeTrendSearchRequest(
    @DateTimeFormat(iso = ISO.DATE) LocalDate from,
    @DateTimeFormat(iso = ISO.DATE) LocalDate to,
    String unit
) {

}
