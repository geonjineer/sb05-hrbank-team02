package com.sprint.project.hrbank.dto.employee;

import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.clampSize;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeSortDirection;
import static com.sprint.project.hrbank.normalizer.SearchRequestNormalizer.normalizeString;

import com.sprint.project.hrbank.entity.EmployeeStatus;
import com.sprint.project.hrbank.validation.DateRange;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@DateRange(from = "hireDateFrom", to = "hireDateTo")
public record EmployeeSearchRequest(
    String nameOrEmail,
    String employeeNumber,
    String departmentName,
    String position,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDateFrom,

    @PastOrPresent(message = "DATE_PAST_OR_PRESENT")
    @DateTimeFormat(iso = ISO.DATE)
    LocalDate hireDateTo,

    EmployeeStatus status,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,     // name | employeeNumber | hireDate
    String sortDirection  // asc | desc
) {

  public static EmployeeSearchRequest of(EmployeeSearchRequest r) {
    Integer size = clampSize(r.size, 10, 1, 100);

    Set<String> allowedFields = Set.of("name", "hireDate", "employeeNumber");
    String sortField = normalizeString(r.sortField, allowedFields, "name");
    String sortDirection = normalizeSortDirection(r.sortDirection, "asc");

    return new EmployeeSearchRequest(
        r.nameOrEmail(),
        r.employeeNumber(),
        r.departmentName(),
        r.position(),
        r.hireDateFrom(),
        r.hireDateTo(),
        r.status(),
        r.idAfter(),
        r.cursor(),
        size,
        sortField,
        sortDirection
    );
  }

}
