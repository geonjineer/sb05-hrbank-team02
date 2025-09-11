package com.sprint.project.hrbank.dto.changeLog;

import com.sprint.project.hrbank.entity.ChangeLogType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record ChangeLogSearchRequest(
    String employeeNumber,
    ChangeLogType type,
    String memo,
    String ipAddress,
    @DateTimeFormat(iso = ISO.DATE) LocalDate atFrom,
    @DateTimeFormat(iso = ISO.DATE) LocalDate atTo,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {

}
