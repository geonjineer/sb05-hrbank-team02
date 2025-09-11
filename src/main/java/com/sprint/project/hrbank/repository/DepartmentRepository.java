package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentQueryRepository {

    boolean existsByName(String name);
}
