package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.entity.Backup;
import java.util.List;

public interface BackupQueryRepository {
  List<Backup> search(BackupSearchRequest request, int sizePlusOne, boolean asc,
      String sortField, String lastSortVal, Long lastId);
}