package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.employee.EmployeeCountSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendDto;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendSearchRequest;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeStatsService {

  private final EmployeeRepository employeeRepository;

  private static final Set<String> DATE_UNITS = Set.of("day", "week", "month", "quarter", "year");

  @Transactional(readOnly = true)
  public EmployeeTrendDto getEmployeeTrend(EmployeeTrendSearchRequest request) {
    String unit = DATE_UNITS.contains(request.unit())
        ? request.unit() : "month";
    LocalDate to = request.to() == null ? LocalDate.now() : request.to();
    LocalDate from = request.from() == null ? calculateFrom(unit) : request.from();

    long count = employeeRepository.searchCountByDate(to);
    long prev = employeeRepository.searchCountByDate(from);
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
  public long getEmployeeCount(EmployeeCountSearchRequest request) {
    return employeeRepository.searchCountByDate(request.to());
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
