package com.sprint.project.hrbank.dto.backup;

import com.sprint.project.hrbank.entity.BackupStatus;
import java.time.OffsetDateTime;

/**
 * 백업 목록 조회 파라미터 DTO (컨트롤러 -> 서비스) - withSize/withSortField 같은 체이닝 메서드는 두지 않고, 순수 값 전달만 합니다.
 */
public record BackupSearchRequest(
    String worker,
    BackupStatus status,
    OffsetDateTime startedAtFrom,
    OffsetDateTime startedAtTo,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,    // startedAt | endedAt | status
    String sortDirection // ASC | DESC
) {

}