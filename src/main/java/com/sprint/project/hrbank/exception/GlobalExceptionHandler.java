package com.sprint.project.hrbank.exception;

import com.sprint.project.hrbank.dto.common.ErrorResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final ConstraintErrorResolver resolver;
  private final MessageSource messageSource;

  // 내부 예외 메세지를 노출하지 않기 위해 가공하는 메소드
  private String safeDetail(Exception e) {
    // 추후 필요 시 화이트리스트 기반 메세지 통제
    return e.getMessage();
  }

  private String detail(ErrorCode code, @Nullable String field, String... extras) {
    ArrayList<String> parts = new ArrayList<>();
    parts.add(msg(code)); // 로컬라이즈된 사용자 메시지

    if (field != null && !field.isBlank()) {
      parts.add("[field=" + field + "]");
    }

    for (String ex : extras) {
      if (ex != null && !ex.isBlank()) parts.add("[" + ex + "]");
    }

    return String.join(" ", parts);
  }

  private ErrorCode toErrorCode(String raw) {
    if (raw == null) {
      return ErrorCode.INTERNAL_ERROR;
    }
    String code =
        raw.startsWith("{") && raw.endsWith("}") ? raw.substring(1, raw.length() - 1) : raw;
    for (ErrorCode errorCode : ErrorCode.values()) {
      if (errorCode.name().equals(code)) {
        return errorCode;
      }
    }
    return ErrorCode.INTERNAL_ERROR;
  }

  private ResponseEntity<ErrorResponse> respond(ErrorCode code, String details) {
    return ResponseEntity.status(code.getStatus()).body(
        ErrorResponse.builder()
            .timestamp(Instant.now())
            .message(code.name())
            .status(code.getStatus().value())
            .details(details)
            .build()
    );
  }

  private String msg(ErrorCode code) {
    return messageSource.getMessage(code.name(), null, LocaleContextHolder.getLocale());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    ErrorCode code = e.getCode();
    String details = messageSource.getMessage(code.name(), e.getArgs(),
        LocaleContextHolder.getLocale());

    return respond(code, details);
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
    log.warn("Bad Request: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ErrorResponse.builder()
            .timestamp(Instant.now())
            .message("Bad Request")
            .status(HttpStatus.BAD_REQUEST.value())
            .details(safeDetail(e))
            .build());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(Exception e) {
    log.warn("Not Found: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ErrorResponse.builder()
            .timestamp(Instant.now())
            .message("Not Found")
            .status(HttpStatus.NOT_FOUND.value())
            .details(safeDetail(e))
            .build());
  }

  //FileStorageException 처리 추가
  @ExceptionHandler(FileStorageException.class)
  public ResponseEntity<ErrorResponse> handleFileStorageException(Exception e) {
    log.error("File Storage Error: {}", e.getMessage(), e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ErrorResponse.builder()
            .timestamp(Instant.now())
            .message("File Storage Error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .details(safeDetail(e))
            .build()
    );
  }

  // 검증 실패 (@RequestBody 필드)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("Method Argument Not Valid: {}", e.getMessage(), e);

    // "이름은 필수입니다. [field=name]; 이메일 형식이 올바르지 않습니다. [field=email]" 식으로 통일
    String details = e.getBindingResult().getFieldErrors().stream()
        .map(error -> {
          ErrorCode code = toErrorCode(error.getDefaultMessage());
          return detail(code, error.getField());
        })
        .collect(Collectors.joining(", "));

    return respond(ErrorCode.VALIDATION_FAILED, details);
  }

  // 검증 실패 (@RequestParam, @PathVariable)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
    log.warn("Constraint violation: {}", e.getMessage());

    // 메시지 키를 못 받는 경우가 많아 사용자 문구만 통일
    return respond(ErrorCode.VALIDATION_FAILED, msg(ErrorCode.VALIDATION_FAILED));
  }

  // 타입 불일치
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.error("Type Mismatch: {}", e.getMessage(), e);

    ErrorCode code = ErrorCode.TYPE_MISMATCH;
    String expected = e.getRequiredType() != null ? e.getRequiredType().getName() : null;
    String details = detail(code, e.getName(), expected != null ? "expected=" + expected : null);

    return respond(code, details);
  }

  // DB 제약 위반
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
    log.warn("Data Integrity: {}", e.getMessage(), e);

    ErrorCode code = resolver.resolve(e);

    return respond(code, msg(code));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Internal Server Error: {}", e.getMessage(), e);
    return respond(ErrorCode.INTERNAL_ERROR, msg(ErrorCode.INTERNAL_ERROR));
  }
}
