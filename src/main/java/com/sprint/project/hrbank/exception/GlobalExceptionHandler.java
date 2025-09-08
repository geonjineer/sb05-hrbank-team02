package com.sprint.project.hrbank.exception;

import com.sprint.project.hrbank.dto.common.ErrorResponse;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 내부 예외 메세지를 노출하지 않기 위해 가공하는 메소드
  private String safeDetail(Exception e) {
    // 추후 필요 시 화이트리스트 기반 메세지 통제
    return e.getMessage();
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
    log.warn("Bad Request: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder()
        .timestamp(Instant.now())
        .message("Bad Request")
        .status(HttpStatus.BAD_REQUEST.value())
        .details(safeDetail(e))
        .build());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(Exception e) {
    log.warn("Not Found: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder()
            .timestamp(Instant.now())
            .message("Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .details(safeDetail(e))
        .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleServerError(Exception e) {
    log.error("Internal Server Error: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
            .timestamp(Instant.now())
            .message("Internal Server Error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .details(safeDetail(e))
        .build());
  }

}
