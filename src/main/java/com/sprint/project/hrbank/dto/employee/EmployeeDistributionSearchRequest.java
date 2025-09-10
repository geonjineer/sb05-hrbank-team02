package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;

public record EmployeeDistributionSearchRequest(
    String groupBy,
    EmployeeStatus status
) {

}
