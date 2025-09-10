package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.mapper.DepartmentMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper;

  @Transactional
  public DepartmentDto create(DepartmentCreateRequest req) {
    String name = req.name();
    String description = req.description();
    LocalDate establishedDate = req.establishedDate();

    Department department = Department.builder()
        .name(name).description(description).establishedDate(establishedDate).build();

    Long employeeCount = employeeRepository.countByDepartment(department);
    departmentRepository.save(department);

    return departmentMapper.toDepartmentDto(department, employeeCount);
  }


  @Transactional
  public DepartmentDto find(Long id) {
     return departmentRepository.findById(id)
        .map(department -> {
          Long employeeCount = employeeRepository.countByDepartment(department);
          return departmentMapper.toDepartmentDto(department, employeeCount);
        }).orElse(null);
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

    return departmentMapper.toDepartmentDto(department, employeeRepository.countByDepartment(department));
  }

  @Transactional
  public void delete(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new
            NoSuchElementException("Department not found with id: "
            + id));

    if (employeeRepository.existsByDepartment(department)) {
      throw new IllegalArgumentException("Deletion failed: Employees are still assigned to\n" +
              "  this department.");
    }

    // 3. 부서 삭제
    departmentRepository.deleteById(id);
  }
}
