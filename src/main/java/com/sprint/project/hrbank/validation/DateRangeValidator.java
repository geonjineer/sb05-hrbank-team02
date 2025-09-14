package com.sprint.project.hrbank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

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
      Object from = getField(value, fromField);
      Object to = getField(value, toField);
      if (from == null || to == null) {
        return true;
      }

      boolean ok = compare(from, to, allowEqual);
      if (!ok) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("DATE_RANGE_INVALID")
            .addPropertyNode(toField)
            .addConstraintViolation();
      }
      return ok;
    } catch (Exception e) {
      return true; // 필드 접근 실패 시 검증 패스
    }
  }

  private boolean compare(Object from, Object to, boolean allowEqual) {
    if (from instanceof LocalDate f && to instanceof LocalDate t) {
      int cmp = f.compareTo(t);
      return allowEqual ? cmp <= 0 : cmp < 0;
    }
    if (from instanceof Instant f && to instanceof Instant t) {
      int cmp = f.compareTo(t);
      return allowEqual ? cmp <= 0 : cmp < 0;
    }
    // 타입이 다르거나 지원 안 하면 그냥 통과
    return true;
  }

  private Object getField(Object target, String name) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }
}
