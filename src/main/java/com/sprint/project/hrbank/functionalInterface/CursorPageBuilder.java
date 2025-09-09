package com.sprint.project.hrbank.functionalInterface;

import java.util.List;

@FunctionalInterface
public interface CursorPageBuilder<R, D> {
  R build(
      List<D> content,
      String nextCursor,
      Long nextIdAfter,
      Integer size,
      Long totalElements,
      Boolean hasNext
  );
}
