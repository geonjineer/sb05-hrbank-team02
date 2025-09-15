package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import org.junit.jupiter.api.Test;

class CursorCodecTest {

  @Test
  void encode_and_decode_roundtrip() {
    CursorCodec codec = new CursorCodec(new ObjectMapper());
    CursorPayload payload = new CursorPayload("foo", 123L);

    String cursor = codec.encode(payload);
    CursorPayload decoded = codec.decode(cursor, CursorPayload.class);

    assertThat(decoded).isEqualTo(payload); // record라 structural equality
  }

  @Test
  void decode_invalid_cursor_throws_iae() {
    CursorCodec codec = new CursorCodec(new ObjectMapper());
    assertThatThrownBy(() -> codec.decode("not-base64", CursorPayload.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void encode_allows_nulls() {
    CursorCodec codec = new CursorCodec(new ObjectMapper());
    CursorPayload payload = new CursorPayload(null, null);

    String cursor = codec.encode(payload);
    CursorPayload decoded = codec.decode(cursor, CursorPayload.class);

    assertThat(decoded.sortVal()).isNull();
    assertThat(decoded.id()).isNull();
  }
}
