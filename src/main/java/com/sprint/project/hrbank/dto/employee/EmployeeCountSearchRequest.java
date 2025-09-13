package com.sprint.project.hrbank.dto.employee;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.defaultLocalDate;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@DateRange(from = "fromDate", to = "toDate")
public record EmployeeCountSearchRequest(
    EmployeeStatus employeeStatus,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate fromDate,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate toDate
) {

  public static EmployeeCountSearchRequest of(EmployeeCountSearchRequest r) {
    LocalDate fromDate = defaultLocalDate(r.fromDate(), LocalDate.of(1970, 1, 1));
    LocalDate toDate = defaultLocalDate(r.toDate(), LocalDate.now());

    return new EmployeeCountSearchRequest(
        r.employeeStatus(),
        fromDate,
        toDate
    );
  }

}
