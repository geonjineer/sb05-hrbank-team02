package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.functionalInterface.CursorPageBuilder;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursorPageAssembler {

  private final CursorCodec cursorCodec;

  public <D, R> R assemble(
      List<D> content,
      int size,
      boolean hasNext,
      Function<D, String> sortValFn,  // 정렬 값을 문자열로
      Function<D, Long> inFn,         // 타이브레이커
      CursorPageBuilder<R, D> builder // 타입별 DTO 생성자
  ) {
    String nextCursor = null;
    Long nextIdAfter = null;

    if (hasNext && !content.isEmpty()) {
      D last = content.get(content.size() - 1);
      String sortVal = nullToEmpty(sortValFn.apply(last));
      Long id = inFn.apply(last);
      nextCursor = cursorCodec.encode(new CursorPayload(sortVal, id));
      nextIdAfter = id;
    }

    return builder.build(content, nextCursor, nextIdAfter, size, null, hasNext);
  }

  private static String nullToEmpty(String string) {
    return string == null ? "" : string;
  }
}
