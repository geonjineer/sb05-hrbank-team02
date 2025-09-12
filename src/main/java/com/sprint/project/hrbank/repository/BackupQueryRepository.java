package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import java.time.OffsetDateTime;
import java.util.List;

/*
 * 커서 기반 검색
 * @param worker             부분일치(옵션)
 * @param status             완전일치(옵션)
 * @param startedAtFrom      범위 from(옵션)
 * @param startedAtTo        범위 to(옵션)
 * @param sizePlusOne        page size + 1 (hasNext 판별용)
 * @param sortField          startedAt | endedAt | status
 * @param asc                오름차순 여부
 * @param lastSortVal        커서 정렬값(문자열, 날짜는 ISO-8601 문자열로)
 * @param lastId             커서 타이브레이커 id
 */

public interface BackupQueryRepository {

  List<Backup> search(
      String worker,
      BackupStatus status,
      OffsetDateTime startedAtFrom,
      OffsetDateTime startedAtTo,
      int sizePlusOne,
      String sortField,
      boolean asc,
      String lastSortVal,
      Long lastId
  );
}

