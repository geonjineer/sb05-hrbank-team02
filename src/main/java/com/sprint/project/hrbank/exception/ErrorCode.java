package com.sprint.project.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {

  // ===== Employee =====
  EMPLOYEE_NAME_REQUIRED,
  EMPLOYEE_NAME_TOO_LONG,
  EMPLOYEE_NAME_DUPLICATE(HttpStatus.CONFLICT),

  EMAIL_REQUIRED,
  EMAIL_INVALID,
  EMAIL_TOO_LONG,
  EMPLOYEE_EMAIL_DUPLICATE(HttpStatus.CONFLICT),

  POSITION_TOO_LONG,
  DEPARTMENT_ID_REQUIRED,

  MEMO_TOO_LONG,

  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND),

  // ===== Department =====
  DEPARTMENT_NAME_REQUIRED,
  DEPARTMENT_NAME_TOO_LONG,
  DEPARTMENT_NAME_DUPLICATE(HttpStatus.CONFLICT),
  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
  DEPARTMENT_HAS_EMPLOYEES(HttpStatus.CONFLICT),

  DEPARTMENT_DESCRIPTION_TOO_LONG,

  ESTABLISHED_DATE_REQUIRED,
  ESTABLISHED_DATE_PAST_OR_PRESENT,

  // ===== File =====
  PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND),

  // ===== ChangeLog =====
  CHANGE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND),

  // ===== Common =====
  UNSUPPORTED_UNIT,
  DATE_RANGE_INVALID,
  DATE_REQUIRED,
  DATE_PAST_OR_PRESENT,
  PAGE_SIZE_INVALID,
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
  VALIDATION_FAILED,
  DUPLICATE_KEY(HttpStatus.CONFLICT), // 23505 같은 유니크 위반의 포괄 코드
  FK_VIOLATION(HttpStatus.CONFLICT),    // 23503
  CHECK_VIOLATION, // 23514
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
  ENTITY_ID_MIN,
  TYPE_MISMATCH,


  // 파일
  FILE_EMPTY,                    // 업로드 파일이 비었음
  FILE_META_NOT_FOUND,           // 파일 메타 없음
  FILE_NOT_FOUND_ON_DISK,        // 디스크에 파일 없음
  FILE_STORAGE_IO_ERROR,         // 디스크 I/O 실패(저장/이동/삭제)
  FILE_NOT_FOUND,
  FILE_STORAGE_WRITE_FAILED,

  // 백업
  BACKUP_NOT_NEEDED,             // 변경 없음 → 스킵(실패 아님, 200/202 흐름)
  BACKUP_IN_PROGRESS_CONFLICT,   // 동시에 실행 요청 등
  BACKUP_LATEST_NOT_FOUND,       // 최신 백업 없음(조회 API에서 사용)
  BACKUP_LOG_WRITE_FAILED,        // 실패 처리 중 .log 저장 실패

  INVALID_ARGUMENT;

  @Getter
  private final HttpStatus status;

  ErrorCode() {
    this.status = HttpStatus.BAD_REQUEST;
  }

  ErrorCode(HttpStatus status) {
    this.status = status;
  }
}
