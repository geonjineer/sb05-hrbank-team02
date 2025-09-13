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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
      @RequestPart(name = "employee") @Valid EmployeeCreateRequest request,
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
  public ResponseEntity<EmployeeDto> findById(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id) {
    return ResponseEntity.ok().body(employeeService.findById(id));
  }

  @GetMapping
  public CursorPageResponse<EmployeeDto> findAll(
      @ModelAttribute @Valid EmployeeSearchRequest request) {
    EmployeeSearchRequest filtered = EmployeeSearchRequest.of(request);
    return employeeService.find(filtered);
  }

  @GetMapping("/stats/trend")
  public ResponseEntity<EmployeeTrendDto> findTrend(
      @ModelAttribute @Valid EmployeeTrendSearchRequest request) {
    EmployeeTrendSearchRequest filtered = EmployeeTrendSearchRequest.of(request);
    return ResponseEntity.ok(employeeStatsService.getEmployeeTrend(filtered));
  }

  @GetMapping("/stats/distribution")
  public ResponseEntity<List<EmployeeDistributionDto>> findDistribution(
      @ModelAttribute EmployeeDistributionSearchRequest request) {
    EmployeeDistributionSearchRequest filtered = EmployeeDistributionSearchRequest.of(request);
    return ResponseEntity.ok(employeeStatsService.getEmployeeDistribution(filtered));
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getEmployeeCount(
      @ModelAttribute @Valid EmployeeCountSearchRequest request
  ) {
    EmployeeCountSearchRequest filtered = EmployeeCountSearchRequest.of(request);
    return ResponseEntity.ok(employeeStatsService.getEmployeeCount(filtered));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeDto> update(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id,
      @RequestPart(name = "employee") @Valid EmployeeUpdateRequest request,
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
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id,
      HttpServletRequest httpRequest
  ) {
    employeeService.deleteWithLog(id, httpRequest.getRemoteAddr());
    return ResponseEntity.noContent().build();
  }

}

