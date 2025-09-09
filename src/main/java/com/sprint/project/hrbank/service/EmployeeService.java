package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.mapper.EmployeeMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;

  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request) {
    String name = request.name();
    String email = request.email();
    String position = request.position();
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("Department not found"));
    LocalDate hireDate = request.hireDate();

    Employee employee = new Employee(name, email, hireDate, position, department, null);

    employeeRepository.save(employee);
    return employeeMapper.toDto(employee);
  }

  @Transactional(readOnly = true)
  public EmployeeDto findById(Long id) {
    return employeeMapper.toDto(employeeRepository.findById(id).orElseThrow(
        () -> new NoSuchElementException("Employee not found with id: " + id)
    ));
  }

}
