package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.normalizer.SearchRequestNormalizer;
import java.util.Set;

public record EmployeeDistributionSearchRequest(
    String groupBy,
    EmployeeStatus status
) {

  public static EmployeeDistributionSearchRequest of(EmployeeDistributionSearchRequest r) {
    Set<String> allowedGroupBy = Set.of("department", "position");
    String groupBy = SearchRequestNormalizer.normalizeString(
        r.groupBy(), allowedGroupBy, "department");
    EmployeeStatus status = (r.status == null
        || !r.status().equals(EmployeeStatus.ACTIVE))
        ? EmployeeStatus.ACTIVE : r.status();

    return new EmployeeDistributionSearchRequest(groupBy, status);
  }

}
