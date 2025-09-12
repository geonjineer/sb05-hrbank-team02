package com.sprint.project.hrbank.dto.backup;

/**
 * 목록/단건 응답 DTO (엔티티 -> API 응답 전용)
 */
public record BackupItemDto(
    Long id,
    String worker,
    String startedAt,
    String endedAt,
    String status,
    Long fileId
) {}