package com.sprint.project.hrbank.dto.employee;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.alignToUnitEnd;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.defaultLocalDate;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeString;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.subtractUnits;

import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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

    LocalDate alignedTo = alignToUnitEnd(to, unit);
    LocalDate defaultFrom = subtractUnits(alignedTo, unit, 11); // 총 11개 버킷

    LocalDate from = r.from() != null ? r. from() : defaultFrom;

    return new EmployeeTrendSearchRequest(from, alignedTo, unit);
  }

}
