package com.sprint.project.hrbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import com.sprint.project.hrbank.service.DepartmentService;
import com.sprint.project.hrbank.service.EmployeeService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class HrbankApplicationTests {

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private DepartmentService departmentService;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Test
  void employeeRepositoryTest() {
    Optional<Employee> employee = employeeRepository.findById(1L);
    assertThat(employee).isPresent();

  }

}
