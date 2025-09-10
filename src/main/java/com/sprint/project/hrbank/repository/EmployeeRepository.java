package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee>, EmployeeQueryRepository {
  Long countByDepartment(Department department);
  boolean existsByDepartment(Department department);

}
