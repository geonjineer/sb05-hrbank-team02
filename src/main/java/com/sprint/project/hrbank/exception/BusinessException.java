package com.sprint.project.hrbank.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ErrorCode code;
  private final String field;
  private final Object[] args;

  public BusinessException(ErrorCode code) {
    this(code, null, (Object) null);
  }

  public BusinessException(ErrorCode code, String field) {
    this(code, field, (Object) null);
  }

  public BusinessException(ErrorCode code, String field, Object... args) {
    super(code.name());
    this.code = code;
    this.field = field;
    this.args = args;
  }
}
