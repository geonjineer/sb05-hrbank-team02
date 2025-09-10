package com.sprint.project.hrbank.dto.employee;

import java.time.LocalDate;

// 수정할 데이터만 담는 DTO
public record EmployeeUpdateRequest(
    String name,
    String email,
    LocalDate hireDate,
    String position,
    Long departmentId, // Department 엔티티 대신 ID
    Long profileImageId // File 엔티티 대신 ID

) {

}
