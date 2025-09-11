package com.sprint.project.hrbank.dto.changeLog;

import com.sprint.project.hrbank.entity.ChangeLogType;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ChangeLogDto(
    long id,
    ChangeLogType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {

}
