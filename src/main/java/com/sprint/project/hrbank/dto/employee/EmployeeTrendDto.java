package com.sprint.project.hrbank.dto.employee;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record EmployeeTrendDto(
    LocalDate date,
    Long count,
    Long change,
    Double changeRate
) {

}
