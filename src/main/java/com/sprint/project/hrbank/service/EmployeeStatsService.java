package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.employee.EmployeeDistributionDto;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeGroupCountRow;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendDto;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendSearchRequest;
import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeStatsService {

  private final EmployeeRepository employeeRepository;

  private static final Set<String> DATE_UNITS = Set.of("day", "week", "month", "quarter", "year");
  private static final Set<String> GROUP_BY = Set.of("department", "position");

  @Transactional(readOnly = true)
  public EmployeeTrendDto getEmployeeTrend(EmployeeTrendSearchRequest request) {
    String unit = DATE_UNITS.contains(request.unit())
        ? request.unit() : "month";
    LocalDate to = request.to() == null ? LocalDate.now() : request.to();
    LocalDate from = request.from() == null ? calculateFrom(unit) : request.from();

    long count = employeeRepository.searchCount(to);
    long prev = employeeRepository.searchCount(from);
    long change = count - prev;
    double changeRate = count == 0 ? 0.0
        : Math.round((change / (double) prev * 100) * 10) / 10.0;

    return EmployeeTrendDto.builder()
        .date(to)
        .count(count)
        .change(change)
        .changeRate(changeRate)
        .build();
  }

  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> getEmployeeDistribution(
      EmployeeDistributionSearchRequest request) {

    String groupKey = GROUP_BY.contains(request.groupBy())
        ? request.groupBy() : "department";
    EmployeeStatus status = request.status() == null ? EmployeeStatus.ACTIVE : request.status();

    long totalCount = employeeRepository.countTotalByStatus(status);
    List<EmployeeGroupCountRow> rows = employeeRepository.searchCountByGroup(groupKey, status);

    return rows.stream()
        .map(r -> {
          long count = r.count() == null ? 0 : r.count();
          double percentage = totalCount == 0 ? 0.0
              : Math.round((count * 100.0 / totalCount) * 10) / 10.0;
          return EmployeeDistributionDto.builder()
              .groupKey(r.groupKey())
              .count(count)
              .percentage(percentage)
              .build();
        })
        .toList();
  }

  private LocalDate calculateFrom(String unit) {
    return switch (unit) {
      case "day" -> LocalDate.now().minusDays(12);
      case "week" -> LocalDate.now().minusWeeks(12);
      case "month" -> LocalDate.now().minusMonths(12);
      case "quarter" -> LocalDate.now().minusMonths(12 * 3);
      case "year" -> LocalDate.now().minusYears(12);
      default -> LocalDate.now().minusMonths(12);
    };
  }

}
