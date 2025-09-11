package com.sprint.project.hrbank.dto.changeLog;

import lombok.Builder;

@Builder
public record DiffDto(
    String propertyName,
    String before,
    String after
) {

}
