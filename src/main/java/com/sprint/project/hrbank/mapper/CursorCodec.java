package com.sprint.project.hrbank.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursorCodec {

  private final ObjectMapper objectMapper;

  public String encode(Object node) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(node);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
    } catch (Exception e) {
      throw new IllegalArgumentException("cursor encoding failed", e);
    }
  }

  public <T> T decode(String cursor, Class<T> type) {
    try {
      byte[] bytes = Base64.getUrlDecoder().decode(cursor);
      return objectMapper.readValue(bytes, type);
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 cursor", e);
    }
  }

  public record CursorPayload(String sortVal, Long id) {

  }
}
