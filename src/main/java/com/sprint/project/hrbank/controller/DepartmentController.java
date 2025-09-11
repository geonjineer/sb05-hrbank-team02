package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")// 1234
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<DepartmentDto> create(@RequestBody DepartmentCreateRequest req) {
    DepartmentDto created = departmentService.create(req);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  @GetMapping
  public CursorPageResponse<DepartmentDto> findAll(
      @ModelAttribute DepartmentSearchRequest request) {
    return departmentService.findAll(request);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> findById(@PathVariable Long id) {
    DepartmentDto department = departmentService.find(id);
    if (department == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found");
    }
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(department);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> update(
      @PathVariable Long id,
      @RequestBody DepartmentUpdateRequest request) {
    return
        ResponseEntity.ok().body(departmentService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    departmentService.delete(id);
    return
        ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


}
