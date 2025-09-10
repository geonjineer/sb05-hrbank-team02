package com.sprint.project.hrbank.dto.employee;

import java.time.LocalDate;

public record EmployeeTrendSearchRequest(
    LocalDate from,
    LocalDate to,
    String unit
) {

}
