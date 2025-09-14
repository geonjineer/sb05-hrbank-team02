package com.sprint.project.hrbank.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {

  String message() default "DATE_RANGE_INVALID";

  String from();

  String to();

  boolean allowEqual() default true;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
