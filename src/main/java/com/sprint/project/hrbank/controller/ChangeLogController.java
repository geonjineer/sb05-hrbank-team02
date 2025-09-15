package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogCountSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDto;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.DiffDto;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.service.ChangeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @Operation(summary = "직원 정보 수정 이력 목록 조회")
  @GetMapping
  public CursorPageResponse<ChangeLogDto> getChangeLogs(
      @ModelAttribute @Valid ChangeLogSearchRequest request
  ) {
    ChangeLogSearchRequest filtered = ChangeLogSearchRequest.of(request);

    return changeLogService.getChangeLogs(filtered);
  }

  @Operation(summary = "직원 정보 수정 이력 상세 조회")
  @GetMapping("/{id}/diffs")
  public List<DiffDto> getDiff(
      @PathVariable @Positive(message = "ENTITY_ID_MIN") Long id) {
    return changeLogService.findDiffsByChangeLogId(id);
  }

  @Operation(summary = "수정 이력 건수 조회")
  @GetMapping("/count")
  public Long count(
      @ModelAttribute @Valid ChangeLogCountSearchRequest request
  ) {
    ChangeLogCountSearchRequest filtered = ChangeLogCountSearchRequest.of(request);

    return changeLogService.findCountByDateBetween(filtered);
  }
}
