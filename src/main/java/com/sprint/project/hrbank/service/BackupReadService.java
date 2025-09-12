package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.backup.BackupItemDto;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import com.sprint.project.hrbank.mapper.BackupMapper;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.repository.BackupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackupReadService {

  private final BackupRepository backupRepository; // 커스텀 search 포함
  private final BackupMapper backupMapper;
  private final CursorCodec cursorCodec;

  public BackupItemDto latest(BackupStatus status) {
    return backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .map(backupMapper::toDto)
        .orElse(null);
  }

  public CursorPageResponse<BackupItemDto> search(BackupSearchRequest req) {
    // size 보정
    int rawSize = (req.size() == null || req.size() <= 0) ? 10 : req.size();
    int size = Math.min(rawSize, 100);

    // 정렬 필드/방향 보정
    final String sortField = normalizeSortField(req.sortField());
    final boolean asc = !"DESC".equalsIgnoreCase(req.sortDirection());

    // 커서 파싱
    String lastSortVal = null;
    Long lastId = null;
    if (req.cursor() != null && !req.cursor().isBlank()) {
      CursorPayload p = cursorCodec.decode(req.cursor(), CursorPayload.class);
      lastSortVal = p.sortVal();
      lastId = p.id();
    } else if (req.idAfter() != null) {
      // idAfter만 있을 경우 키 값은 null로 두고 id 비교만 동작
      lastId = req.idAfter();
    }

    // DB 조회(size+1) – 정렬키+id 커서를 DB에서 비교
    List<Backup> rows = backupRepository.search(
        req.worker(),
        req.status(),
        req.startedAtFrom(),
        req.startedAtTo(),
        size + 1,
        sortField,
        asc,
        lastSortVal,
        lastId
    );

    boolean hasNext = rows.size() > size;
    if (hasNext) {
      rows = rows.subList(0, size);
    }

    // DTO 변환
    List<BackupItemDto> content = rows.stream()
        .map(backupMapper::toDto)
        .toList();

    // nextCursor 계산(마지막 요소의 정렬키 문자열 + id)
    String nextCursor = null;
    Long nextIdAfter = null;
    if (!content.isEmpty()) {
      BackupItemDto last = content.get(content.size() - 1);
      String sortValForCursor = switch (sortField) {
        case "endedAt"  -> nvl(last.endedAt());
        case "status"   -> last.status() == null ? "" : last.status();
        default         -> nvl(last.startedAt());
      };
      nextCursor = cursorCodec.encode(new CursorPayload(sortValForCursor, last.id()));
      nextIdAfter = last.id();
    }

    return CursorPageResponse.<BackupItemDto>builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter)
        .size(size)
        .totalElements(null) // 커서 페이징에서는 보통 제공하지 않음
        .hasNext(hasNext)
        .build();
  }

  private String nvl(String s) { return s == null ? "" : s; }

  private String normalizeSortField(String f) {
    if ("endedAt".equals(f))  return "endedAt";
    if ("status".equals(f))   return "status";
    return "startedAt"; // default
  }
}