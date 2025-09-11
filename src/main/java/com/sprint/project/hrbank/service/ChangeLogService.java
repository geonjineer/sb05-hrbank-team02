package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogCreateRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDiffCreate;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDto;
import com.sprint.project.hrbank.dto.changeLog.DiffDto;
import com.sprint.project.hrbank.entity.ChangeLog;
import com.sprint.project.hrbank.entity.ChangeLogDiff;
import com.sprint.project.hrbank.mapper.ChangeLogDiffMapper;
import com.sprint.project.hrbank.mapper.ChangeLogMapper;
import com.sprint.project.hrbank.repository.ChangeLogRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogMapper changeLogMapper;
  private final ChangeLogDiffMapper changeLogDiffMapper;

  @Transactional
  public ChangeLogDto create(ChangeLogCreateRequest request) {
    ChangeLog changeLog = new ChangeLog(
        request.type(),
        request.memo(),
        request.employeeNumber(),
        request.ipAddress(),
        request.at()
    );

    if (request.diffs() != null) {
      for (ChangeLogDiffCreate d : request.diffs()) {
        ChangeLogDiff diff = new ChangeLogDiff(
            d.property(),
            d.before(),
            d.after()
        );
        changeLog.addDiff(diff);
      }
    }

    // 부모만 save -> cascade 옵션으로 자식까지 INSERT
    return changeLogMapper.toDto(changeLogRepository.save(changeLog));
  }

  @Transactional
  public List<DiffDto> findDiffsByChangeLogId(Long id) {
    return validateId(id).getDiffs().stream()
        .map(changeLogDiffMapper::toDto)
        .toList();
  }

  private ChangeLog validateId(Long id) {
    return changeLogRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("change log id not found: " + id));
  }
}
