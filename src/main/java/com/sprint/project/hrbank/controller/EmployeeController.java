package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.employee.EmployeeCountSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionDto;
import com.sprint.project.hrbank.dto.employee.EmployeeDistributionSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendDto;
import com.sprint.project.hrbank.dto.employee.EmployeeTrendSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeUpdateRequest;
import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.service.EmployeeService;
import com.sprint.project.hrbank.service.EmployeeStatsService;
import com.sprint.project.hrbank.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;
  private final EmployeeStatsService employeeStatsService;
  private final FileService fileService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> create(
      @RequestPart EmployeeCreateRequest request,
      @RequestPart(required = false) MultipartFile profile,
      HttpServletRequest httpRequest) {
    FileResponse fileResponse = profile == null
        ? null
        : fileService.upload(profile);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(employeeService.createWithLog(request, fileResponse, httpRequest.getRemoteAddr()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> findById(@PathVariable long id) {
    return ResponseEntity.ok().body(employeeService.findById(id));
  }

  @GetMapping
  public CursorPageResponse<EmployeeDto> findAll(
      @ModelAttribute EmployeeSearchRequest request) {
    return employeeService.find(request);
  }

  @GetMapping("/stats/trend")
  public ResponseEntity<EmployeeTrendDto> findTrend(
      @ModelAttribute EmployeeTrendSearchRequest request) {
    return ResponseEntity.ok(employeeStatsService.getEmployeeTrend(request));
  }

  @GetMapping("/stats/distribution")
  public ResponseEntity<List<EmployeeDistributionDto>> findDistribution(
      @ModelAttribute EmployeeDistributionSearchRequest request) {
    return ResponseEntity.ok(employeeStatsService.getEmployeeDistribution(request));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getEmployeeCount(
      @ModelAttribute EmployeeCountSearchRequest request
  ) {
    return ResponseEntity.ok(employeeStatsService.getEmployeeCount(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeDto> update(
      @PathVariable long id,
      @RequestPart EmployeeUpdateRequest request,
      @RequestPart(required = false) MultipartFile profile,
      HttpServletRequest httpRequest
  ) {
    FileResponse fileResponse = profile == null
        ? null
        : fileService.upload(profile);

    return ResponseEntity.ok(
        employeeService.updateWithLog(id, request, fileResponse, httpRequest.getRemoteAddr()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable long id,
      HttpServletRequest httpRequest
  ) {
    employeeService.deleteWithLog(id, httpRequest.getRemoteAddr());
    return ResponseEntity.noContent().build();
  }

}

