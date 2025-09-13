package com.sprint.project.hrbank.dto.backup;

/**
 * 목록/단건 응답 DTO
 */
public record BackupDto(
    Long id,
    String worker,
    String startedAt, // ISO-8601 문자열
    String endedAt,   // ISO-8601 문자열
    String status,
    Long fileId
) {

}