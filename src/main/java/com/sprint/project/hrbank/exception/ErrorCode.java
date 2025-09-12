package com.sprint.project.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // ===== Employee =====
  NAME_REQUIRED,
  NAME_TOO_LONG,
  EMPLOYEE_NAME_DUPLICATE(HttpStatus.CONFLICT),

  EMAIL_REQUIRED,
  EMAIL_INVALID,
  EMAIL_TOO_LONG,
  EMPLOYEE_EMAIL_DUPLICATE(HttpStatus.CONFLICT),

  DEPARTMENT_ID_MIN,

  POSITION_TOO_LONG,

  HIRE_DATE_REQUIRED,
  HIRE_DATE_PAST_OR_PRESENT,

  MEMO_TOO_LONG,

  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND),

  // ===== Department =====
  DEPARTMENT_NAME_REQUIRED,
  DEPARTMENT_NAME_TOO_LONG,
  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND),

  DEPARTMENT_DESCRIPTION_TOO_LONG,

  ESTABLISHED_DATE_REQUIRED,
  ESTABLISHED_DATE_PAST_OR_PRESENT,

  // ===== File =====
  PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND),

  // ===== Common =====
  DATE_RANGE_INVALID(HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
  DUPLICATE_KEY(HttpStatus.CONFLICT), // 23505 같은 유니크 위반의 포괄 코드
  FK_VIOLATION(HttpStatus.CONFLICT),    // 23503
  CHECK_VIOLATION(HttpStatus.BAD_REQUEST), // 23514
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
  TYPE_MISMATCH(HttpStatus.BAD_REQUEST);

  @Getter
  private final HttpStatus status;

  ErrorCode() {
    this.status = HttpStatus.BAD_REQUEST;
  }

  ErrorCode(HttpStatus status) {
    this.status = status;
  }
}
