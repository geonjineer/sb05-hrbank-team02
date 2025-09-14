package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EmployeeCreateRequestValidationTest {

  private static Validator validator;
  private static ValidatorFactory factory;

  @BeforeAll
  static void setUp() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    factory.close();
  }

  @Test
  void validRequest_passes() {
    var req = new EmployeeCreateRequest(
        "홍길동",
        "hong@example.com",
        1L,
        "개발자",                  // 선택 필드
        LocalDate.now(),          // 과거/오늘 허용
        "메모"                     // 선택 필드
    );
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void missingRequiredFields_fails() {
    var bad = new EmployeeCreateRequest(
        "",                        // @NotBlank name
        "not-an-email",            // @Email
        null,                      // @NotNull departmentId
        null,                      // position optional
        null,                      // @NotNull hireDate
        "x".repeat(600)            // memo too long(>500)
    );
    var violations = validator.validate(bad);
    // 최소 4가지 이상 위반이 잡혀야 함
    assertThat(violations.size()).isGreaterThanOrEqualTo(4);
  }
}
