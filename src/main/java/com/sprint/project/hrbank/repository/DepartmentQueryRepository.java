package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.entity.Department;
import io.micrometer.common.lang.Nullable;
import java.time.LocalDate;
import java.util.List;

public interface DepartmentQueryRepository {

  List<Department> search(
      DepartmentSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc,
      @Nullable String lastSortVal,
      @Nullable Long lastId
  );

  Long searchCount(@Nullable LocalDate establishedDate);

}
