package com.sprint.project.hrbank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.Instant;

public class InstantRangeValidator implements ConstraintValidator<DateRange, Object> {

  private String fromField;
  private String toField;
  private boolean allowEqual;

  @Override
  public void initialize(DateRange constraintAnnotation) {
    this.fromField = constraintAnnotation.from();
    this.toField = constraintAnnotation.to();
    this.allowEqual = constraintAnnotation.allowEqual();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    try {
      Instant from = (Instant) getField(value, fromField);
      Instant to = (Instant) getField(value, toField);
      if (from == null || to == null) {
        return true;
      }

      boolean ok = allowEqual ? !from.isAfter(to) : from.isBefore(to);
      if (!ok) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("DATE_RANGE_INVALID")
            .addPropertyNode(toField).addConstraintViolation();
      }
      return ok;
    } catch (Exception e) {
      return true; // 필드 접근 실패 시 검증 패스
    }
  }

  private Object getField(Object target, String name) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }
}
