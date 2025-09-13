package com.sprint.project.hrbank.dto.employee;

import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.calculateFromUnit;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.defaultLocalDate;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeString;

@DateRange(from = "from", to = "to")
public record EmployeeTrendSearchRequest(
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate from,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate to,

    String unit
) {
  public static EmployeeTrendSearchRequest of(EmployeeTrendSearchRequest r) {
    Set<String> allowedUnit = Set.of("day", "week", "month", "quarter", "year");
    String unit = normalizeString(r.unit(), allowedUnit, "month");
    LocalDate to = defaultLocalDate(r.to(), LocalDate.now());
    LocalDate from = calculateFromUnit(to, unit, 12);

    return new EmployeeTrendSearchRequest(from, to, unit);
  }

}
