package com.sprint.project.hrbank.dto.common;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String details
) {

}
