package com.sprint.project.hrbank.dto.changeLog;

import com.sprint.project.hrbank.entity.ChangeLogType;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record ChangeLogCreateRequest(
    ChangeLogType type,
    String employeeNumber,
    List<ChangeLogDiffCreate> diffs,
    String ipAddress,
    Instant at,
    String memo
) {

}
