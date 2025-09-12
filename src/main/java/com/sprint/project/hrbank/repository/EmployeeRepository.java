package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeQueryRepository {

  Long countByDepartment(Department department);

  boolean existsByDepartment(Department department);

  boolean existsByDepartmentId(Long id);

  boolean existsByName(String name);

  boolean existsByEmail(String email);
}
