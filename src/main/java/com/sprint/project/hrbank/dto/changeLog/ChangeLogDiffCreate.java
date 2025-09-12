package com.sprint.project.hrbank.dto.changeLog;

import com.fasterxml.jackson.databind.JsonNode;

public record ChangeLogDiffCreate(
    String property,
    JsonNode before,
    JsonNode after
) {

}
