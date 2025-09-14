package com.sprint.project.hrbank.service;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.alignToUnitEnd;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.nextPeriodEnd;

import com.sprint.project.hrbank.dto.employee.EmployeeCountSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionDto;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeGroupCountRow;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendDto;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendSearchRequest;
import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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
  public List<EmployeeTrendDto> getEmployeeTrend(EmployeeTrendSearchRequest request) {

    String unit = request.unit();
    LocalDate to = request.to();
    LocalDate from = request.from();

    List<LocalDate> periodEnds = buildPeriodEnds(from, to, unit);

    List<EmployeeTrendDto> result = new ArrayList<>(periodEnds.size());
    Long prev = null;

    for (LocalDate end : periodEnds) {
      long count = employeeRepository.searchCountByDate(end);
      long change = (prev == null) ? 0 : (count - prev);
      double changeRate = (prev == null || prev == 0)
          ? 0.0 : round1((change / (double) prev) * 100.0);

      result.add(EmployeeTrendDto.builder()
          .date(end)
          .count(count)
          .change(change)
          .changeRate(changeRate)
          .build());

      prev = count;
    }

    return result;
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
    return employeeRepository.searchCountByDateBetween(
        request.employeeStatus(), request.fromDate(), request.toDate());
  }

  // helpers

  private static double round1(double number) {
    return Math.round(number * 10.0) / 10.0;
  }

  private static List<LocalDate> buildPeriodEnds(LocalDate from, LocalDate to, String unit) {
    LocalDate cursor = alignToUnitEnd(from, unit);
    LocalDate end = alignToUnitEnd(to, unit);

    // from이 버킷 끝 이후면 다음 버킷으로 이동
    while (cursor.isBefore(from)) {
      cursor = nextPeriodEnd(cursor, unit);
    }

    List<LocalDate> ends = new ArrayList<>();
    while (!cursor.isAfter(end)) {
      ends.add(cursor);
      cursor = nextPeriodEnd(cursor, unit);
    }

    return ends;
  }
}
