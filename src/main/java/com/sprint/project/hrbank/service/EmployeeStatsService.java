package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.employee.EmployeeCountSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionDto;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeGroupCountRow;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendDto;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendSearchRequest;
import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.exception.BusinessException;
import com.sprint.project.hrbank.exception.ErrorCode;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeStatsService {

  private final EmployeeRepository employeeRepository;

  @Transactional(readOnly = true)
  public EmployeeTrendDto getEmployeeTrend(EmployeeTrendSearchRequest request) {

    LocalDate to = request.to();
    LocalDate from = request.from();

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
  public List<EmployeeDistributionDto> getEmployeeDistribution(
      EmployeeDistributionSearchRequest request) {

    String groupKey = request.groupBy();
    EmployeeStatus status = request.status();

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

  @Transactional(readOnly = true)
  public long getEmployeeCount(EmployeeCountSearchRequest request) {
    LocalDate from = request.fromDate() == null
        ? LocalDate.of(1970, 1, 1)
        : request.fromDate();
    LocalDate to = request.toDate() == null
        ? LocalDate.now()
        : request.toDate();

    if (from.isAfter(to)) {
      log.warn("date range invalid: from {} to {}", from, to);
      throw new BusinessException(ErrorCode.DATE_RANGE_INVALID, "dateRange", "fromDate", "toDate");
    }

    return employeeRepository.searchCountByDateBetween(request.employeeStatus(), from, to);
  }

}
