package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
@Tag(name = "부서 관리", description = "부서 관리 API")
public class DepartmentController {

  private final DepartmentService departmentService;

  @Operation(summary = "부서 등록")
  @PostMapping
  public ResponseEntity<DepartmentDto> create(
      @RequestBody @Valid DepartmentCreateRequest req) {
    DepartmentDto created = departmentService.create(req);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  @Operation(summary = "부서 목록 조회")
  @GetMapping
  public CursorPageResponse<DepartmentDto> findAll(
      @ModelAttribute DepartmentSearchRequest request) {
    DepartmentSearchRequest filtered = DepartmentSearchRequest.of(request);

    return departmentService.findAll(filtered);
  }

  @Operation(summary = "부서 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> findById(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id) {
    DepartmentDto department = departmentService.find(id);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(department);
  }

  @Operation(summary = "부서 수정")
  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> update(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id,
      @RequestBody @Valid DepartmentUpdateRequest request) {
    return
        ResponseEntity.ok().body(departmentService.update(id, request));
  }

  @Operation(summary = "부서 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id) {
    departmentService.delete(id);
    return
        ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
