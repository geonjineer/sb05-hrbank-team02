package com.sprint.project.hrbank.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DepartmentCreateRequest(

    @NotBlank(message = "부서명은 필수입니다.")
    @Size(max = 100, message = "부서명은 최대 50자까지 가능합니다.")
    String name,

    @Size(max = 1000, message = "설명은 최대 1000자까지 가능합니다.")
    String description,

    LocalDate establishedDate
) {

}
