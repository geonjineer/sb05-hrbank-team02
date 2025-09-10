package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.entity.Employee;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeQueryRepository {

  List<Employee> search(
      EmployeeSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc,
      @Nullable String lastSortVal,
      @Nullable Long lastId
  );

  Long searchCount(LocalDate date);

}
