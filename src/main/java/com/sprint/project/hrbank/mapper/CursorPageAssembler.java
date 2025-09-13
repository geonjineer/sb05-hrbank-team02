package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
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

  /**
   * 범용 어셈블러: 커서 자동 생성 + 커스텀 결과 타입 R로 조립
   */
  public <D, R> R assemble(
      List<D> content,
      int size,
      boolean hasNext,
      Function<D, String> sortValFn,   // 정렬 키 문자열
      Function<D, Long> inFn,          // 타이브레이커(ID)
      CursorPageBuilder<R, D> builder  // 결과 R 빌더
  ) {
    Next n = computeNext(content, hasNext, sortValFn, inFn);
    return builder.build(content, n.cursor(), n.idAfter(), size, null, hasNext);
  }

  /**
   * 옵션 2-확장: 표준 응답 타입 CursorPageResponse<D>로 바로 조립
   */
  public <D> CursorPageResponse<D> toCursorPage(
      List<D> content,
      int size,
      boolean hasNext,
      Function<D, String> sortValFn,
      Function<D, Long> idFn
  ) {
    Next n = computeNext(content, hasNext, sortValFn, idFn);

    return CursorPageResponse.<D>builder()
        .content(content)
        .size(size)
        .hasNext(hasNext)
        .nextCursor(n.cursor())
        .nextIdAfter(n.idAfter())
        .build();
  }

  // --- 내부 헬퍼들 ---

  private <D> Next computeNext(
      List<D> content,
      boolean hasNext,
      Function<D, String> sortValFn,
      Function<D, Long> idFn
  ) {
    if (hasNext && !content.isEmpty()) {
      D last = content.get(content.size() - 1);
      String sortVal = nullToEmpty(sortValFn.apply(last));
      Long id = idFn.apply(last);
      String nextCursor = cursorCodec.encode(new CursorPayload(sortVal, id));
      return new Next(nextCursor, id);
    }
    return new Next(null, null);
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  // 중첩 record는 원래 static이라 static 키워드 불필요
  private record Next(String cursor, Long idAfter) {}
}
