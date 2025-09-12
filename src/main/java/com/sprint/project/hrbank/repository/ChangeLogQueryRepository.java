package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogSearchRequest;
import com.sprint.project.hrbank.entity.ChangeLog;
import java.util.List;

public interface ChangeLogQueryRepository {

  List<ChangeLog> search(ChangeLogSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc,
      String lastSortValue,
      Long Id);

}
