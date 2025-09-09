package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.employee.CursorPageResponse;
import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> create(
      @RequestPart EmployeeCreateRequest request,
      @RequestPart(required = false)MultipartFile profile) {

    return ResponseEntity.ok().body(employeeService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> findById(@PathVariable long id) {
    return ResponseEntity.ok().body(employeeService.findById(id));
  }

  @GetMapping
  public CursorPageResponse<EmployeeDto> findAll(EmployeeSearchRequest request) {
    return employeeService.find(request);
  }
}
