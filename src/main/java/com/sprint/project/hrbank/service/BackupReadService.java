package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.backup.BackupDto;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.mapper.BackupMapper;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.repository.BackupRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackupReadService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final CursorCodec cursorCodec;
  private final CursorPageAssembler pageAssembler;

  /**
   * 커서 기반 검색
   */
  public CursorPageResponse<BackupDto> search(BackupSearchRequest req) {
    // 1) 정렬 필드/방향 보정
    final String sortField = normalizeSortField(req.sortField());
    final boolean asc = "asc".equalsIgnoreCase(req.sortDirection());

    // 2) 페이지 크기 보정
    final int size = Math.max(1, Math.min(req.size(), 100));

    // 3) 커서 파싱
    String lastSortVal = null;
    Long lastId = null;
    if (hasText(req.cursor())) {
      CursorPayload p = cursorCodec.decode(req.cursor(), CursorPayload.class);
      lastSortVal = p.sortVal();
      lastId = p.id();
    } else if (req.idAfter() != null) {
      lastId = req.idAfter();
    }

    // 4) DB 조회(size+1)
    List<Backup> rows = backupRepository.search(
        req, size + 1, asc, sortField, lastSortVal, lastId
    );

    // 5) hasNext & pageContent
    boolean hasNext = rows.size() > size;
    List<Backup> pageContent = hasNext ? rows.subList(0, size) : rows;

    // 6) 엔티티 -> DTO
    List<BackupDto> content = pageContent.stream().map(backupMapper::toDto).toList();

    // 7~8) ✅ 커서/nextIdAfter 자동 생성(Assembler에 위임)
    return pageAssembler.toCursorPage(
        content,
        size,
        hasNext,
        last -> switch (sortField) {
          case "endedAt" -> last.endedAt();
          case "status" -> last.status();
          default -> last.startedAt();
        },
        BackupDto::id
    );
  }

  /**
   * 최신 완료(COMPLETED) 1건을 DTO로 반환 (없으면 null)
   */
  public BackupDto findLatestCompletedOrNull() {
    return backupRepository.findTopByStatusOrderByEndedAtDesc(
            com.sprint.project.hrbank.entity.BackupStatus.COMPLETED)
        .map(backupMapper::toDto)
        .orElse(null);
  }

  /**
   * 주어진 상태의 최신 1건을 DTO로 반환 (유효하지 않으면 COMPLETED)
   */
  public BackupDto findLatestByStatusOrNull(String statusName) {
    com.sprint.project.hrbank.entity.BackupStatus status;
    try {
      status = com.sprint.project.hrbank.entity.BackupStatus.valueOf(statusName.toUpperCase());
    } catch (Exception ignore) {
      status = com.sprint.project.hrbank.entity.BackupStatus.COMPLETED;
    }
    return backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .map(backupMapper::toDto)
        .orElse(null);
  }

  // --- internal helpers ---

  private String normalizeSortField(String f) {
    if (Objects.equals(f, "endedAt")) {
      return "endedAt";
    }
    if (Objects.equals(f, "status")) {
      return "status";
    }
    return "startedAt";
  }

  private boolean hasText(String s) {
    return s != null && !s.isBlank();
  }
}
