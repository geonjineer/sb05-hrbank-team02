package com.sprint.project.hrbank.dto.employee;

import lombok.Builder;

@Builder
public record EmployeeDistributionDto(
    String groupKey,
    Long count,
    Double percentage
) {

}
