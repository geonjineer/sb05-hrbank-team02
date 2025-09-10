package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.mapper.DepartmentMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import com.sprint.project.hrbank.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
  private final EmployeeService employeeService;
  private final EmployeeRepository employeeRepository;

  @Transactional
  public DepartmentDto create(DepartmentCreateRequest req) {
    String name = req.name();
    String description = req.description();
    LocalDate establishedDate = req.establishedDate();

    Department department = Department.builder()
        .name(name).description(description).establishedDate(establishedDate).build();

    departmentRepository.save(department);

    return departmentMapper.toDepartmentDto(department);
  }


  @Transactional
  public DepartmentDto find(Long id) {
    return departmentRepository.findById(id)
        .map(departmentMapper::toDepartmentDto).orElse(null);
  }
  @Transactional
  public DepartmentDto update(Long id, DepartmentUpdateRequest request) {
    Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + id));
    if (!department.getName().equals(request.name()) && departmentRepository.existsByName(request.name())) {
      throw new IllegalArgumentException("Department with name " + request.name() + " already exists.");
    }

    department.setName(request.name());
    department.setDescription(request.description());
    department.setEstablishedDate(request.establishedDate());

    return departmentMapper.toDepartmentDto(department);
  }

  @Transactional
  public void delete(Long id) {
    departmentRepository.findById(id)
            .orElseThrow(() -> new
                    NoSuchElementException("Department not found with id: "
                    + id));

    if (employeeRepository.existsByDepartmentId(id)) {
      throw new IllegalArgumentException("Deletion failed: Employees are still assigned to\n" +
              "  this department.");
    }

    // 3. 부서 삭제
    departmentRepository.deleteById(id);
  }
}
