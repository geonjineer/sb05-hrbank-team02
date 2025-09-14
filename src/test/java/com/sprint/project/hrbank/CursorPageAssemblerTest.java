package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.hrbank.functionalInterface.CursorPageBuilder;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import java.util.List;
import org.junit.jupiter.api.Test;

class CursorPageAssemblerTest {

  record Item(String sortVal, Long id) {

  }

  // 커스텀 결과 컨테이너(테스트용)
  record Result<T>(List<T> content, String nextCursor, Long nextIdAfter, int size, Object total,
                   boolean hasNext) {

  }

  @Test
  void assemble_generates_next_cursor_when_hasNext_and_content_not_empty() {
    var codec = new CursorCodec(new ObjectMapper());
    var assembler = new CursorPageAssembler(codec);

    var content = List.of(new Item("A", 10L), new Item("B", 20L));
    int size = 2;
    boolean hasNext = true;

    String expectedCursor = codec.encode(new CursorPayload("B", 20L));

    CursorPageBuilder<Result<Item>, Item> builder =
        Result::new;

    Result<Item> result = assembler.assemble(
        content, size, hasNext,
        Item::sortVal, Item::id,
        builder
    );

    assertThat(result.nextCursor()).isEqualTo(expectedCursor);
    assertThat(result.nextIdAfter()).isEqualTo(20L);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.content()).hasSize(2);
  }

  @Test
  void assemble_returns_null_cursor_when_hasNext_false_or_empty_content() {
    var codec = new CursorCodec(new ObjectMapper());
    var assembler = new CursorPageAssembler(codec);

    var builder = (CursorPageBuilder<Result<Item>, Item>)
        Result::new;

    // hasNext=false
    var content = List.of(new Item("A", 1L));
    var r1 = assembler.assemble(content, 1, false, Item::sortVal, Item::id, builder);
    assertThat(r1.nextCursor()).isNull();
    assertThat(r1.nextIdAfter()).isNull();
    assertThat(r1.hasNext()).isFalse();

    // content empty
    var r2 = assembler.assemble(List.<Item>of(), 1, true, Item::sortVal, Item::id, builder);
    assertThat(r2.nextCursor()).isNull();
    assertThat(r2.nextIdAfter()).isNull();
  }

  @Test
  void assemble_normalizes_null_sortVal_to_empty_string_in_payload() {
    var codec = new CursorCodec(new ObjectMapper());
    var assembler = new CursorPageAssembler(codec);

    var content = List.of(new Item(null, 30L)); // sortVal == null
    String expectedCursor = codec.encode(new CursorPayload("", 30L)); // null → ""

    var builder = (CursorPageBuilder<Result<Item>, Item>)
        Result::new;

    var res = assembler.assemble(content, 1, true, Item::sortVal, Item::id, builder);
    assertThat(res.nextCursor()).isEqualTo(expectedCursor);
    assertThat(res.nextIdAfter()).isEqualTo(30L);
  }
}
