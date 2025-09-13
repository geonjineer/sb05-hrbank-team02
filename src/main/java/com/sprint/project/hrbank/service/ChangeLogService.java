package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogCountSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogCreateRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDiffCreate;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDto;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogSearchRequest;
import com.sprint.project.hrbank.dto.changeLog.DiffDto;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.entity.ChangeLog;
import com.sprint.project.hrbank.entity.ChangeLogDiff;
import com.sprint.project.hrbank.exception.BusinessException;
import com.sprint.project.hrbank.exception.ErrorCode;
import com.sprint.project.hrbank.mapper.ChangeLogDiffMapper;
import com.sprint.project.hrbank.mapper.ChangeLogMapper;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.repository.ChangeLogRepository;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogMapper changeLogMapper;
  private final ChangeLogDiffMapper changeLogDiffMapper;
  private final CursorCodec cursorCodec;
  private final CursorPageAssembler cursorPageAssembler;

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

  @Transactional(readOnly = true)
  public CursorPageResponse<ChangeLogDto> getChangeLogs(ChangeLogSearchRequest request) {

    int size = request.size();
    String sortField = request.sortField();
    boolean desc = !"asc".equalsIgnoreCase(request.sortDirection());

    String lastSortValue = null;
    Long lastId = null;
    String cursor = request.cursor();

    if (cursor != null && !cursor.isBlank()) {
      CursorPayload c = cursorCodec.decode(cursor, CursorPayload.class);
      lastSortValue = c.sortVal();
      lastId = c.id();
    } else if (request.idAfter() != null) {
      Long idAfter = request.idAfter();
      ChangeLog last = validateId(idAfter);

      lastSortValue = switch (sortField) {
        case "ipAddress" -> last.getIpAddress();
        case "at" -> last.getAt().toString();
        default -> "";
      };
      lastId = last.getId();
    }

    List<ChangeLogDto> content = changeLogRepository.search(request, size + 1,
            sortField, desc,
            lastSortValue, lastId).stream()
        .map(changeLogMapper::toDto)
        .toList();

    boolean hasNext = content.size() > size;
    if (hasNext) {
      content = content.subList(0, size);
    }

    Function<ChangeLogDto, String> sortValFn = c ->
        switch (sortField) {
          case "ipAddress" -> c.ipAddress() == null ? "" : c.ipAddress();
          case "at" -> c.at() == null ? "" : c.at().toString();
          default -> "";
        };

    return cursorPageAssembler.assemble(
        content,
        size,
        hasNext,
        sortValFn,
        ChangeLogDto::id,
        CursorPageResponse<ChangeLogDto>::new
    );
  }

  @Transactional(readOnly = true)
  public List<DiffDto> findDiffsByChangeLogId(Long id) {
    return validateId(id).getDiffs().stream()
        .map(changeLogDiffMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public Long findCountByDateBetween(ChangeLogCountSearchRequest request) {
    return changeLogRepository.countChangeLogByAtBetween(request.fromDate(), request.toDate());
  }


  private ChangeLog validateId(Long id) {
    return changeLogRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("change log not found id: {}", id);
          return new BusinessException(ErrorCode.CHANGE_LOG_NOT_FOUND, "changeLogId");
        });
  }

}
