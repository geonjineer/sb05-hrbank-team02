package com.sprint.project.hrbank.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class CursorCodec {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public String encode(Object node) {
    try {
      return Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(node));
    } catch (Exception e) {
      throw new IllegalArgumentException("cursor encoding failed", e);
    }
  }

  public <T> T decode(String cursor, Class<T> type) {
    try {
      byte[] bytes = Base64.getDecoder().decode(cursor);
      return objectMapper.readValue(bytes, type);
    } catch (Exception e) {
      throw new IllegalArgumentException("유효하지 않은 cursor", e);
    }
  }

  public record CursorPayload(String sortVal, Long id) {

  }
}
