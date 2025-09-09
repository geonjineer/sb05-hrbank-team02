package com.sprint.project.hrbank.dto.employee;

import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    Boolean hasNext
) {

}
