package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.mapper.DepartmentMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

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
}
