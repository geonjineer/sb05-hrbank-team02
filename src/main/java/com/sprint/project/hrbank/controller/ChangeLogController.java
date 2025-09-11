package com.sprint.project.hrbank.controller;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogCountSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDto;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.DiffDto;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.service.ChangeLogService;
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
public class ChangeLogController {

  private final ChangeLogService changeLogService;

  @GetMapping
  public CursorPageResponse<ChangeLogDto> getChangeLogs(
      @ModelAttribute ChangeLogSearchRequest request
  ) {
    return null;
  }

  @GetMapping("/{id}/diffs")
  public List<DiffDto> getDiff(@PathVariable Long id) {
    return changeLogService.findDiffsByChangeLogId(id);
  }

  @GetMapping("/count")
  public Long count(
      @ModelAttribute ChangeLogCountSearchRequest request
  ) {
    return changeLogService.findCountByDateBetween(request);
  }
}
