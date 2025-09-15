package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.backup.BackupDto;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import com.sprint.project.hrbank.exception.BusinessException;
import com.sprint.project.hrbank.exception.ErrorCode;
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
    // 정렬 필드/방향 보정
    final String sortField = normalizeSortField(req.sortField());
    final boolean asc = "asc".equalsIgnoreCase(req.sortDirection());

    // 페이지 크기 보정 (하한 1, 상한 100)
    final int size = Math.max(1, Math.min(req.size(), 100));

    // 커서 파싱
    String lastSortVal = null;
    Long lastId = null;
    if (hasText(req.cursor())) {
      CursorPayload p = cursorCodec.decode(req.cursor(), CursorPayload.class);
      lastSortVal = p.sortVal();
      lastId = p.id();
    } else if (req.idAfter() != null) {
      lastId = req.idAfter();
    }

    // DB 조회(size+1) – 정렬키+id 커서를 DB에서 비교
    List<Backup> rows = backupRepository.search(
        req,          // 검색 필터(작업자/상태/기간/정렬 등)
        size + 1,     // 다음 페이지 유무 판단용 한 건 추가 조회
        asc,
        sortField,
        lastSortVal,
        lastId
    );

    // hasNext 판정 + 페이지 컨텐츠 절단
    boolean hasNext = rows.size() > size;
    List<Backup> pageContent = hasNext ? rows.subList(0, size) : rows;

    // 엔티티 -> DTO 변환
    List<BackupDto> content = pageContent.stream().map(backupMapper::toDto).toList();

    // CursorPageAssembler를 사용해 nextCursor/nextIdAfter 조립
    return pageAssembler.toCursorPage(
        content,
        size,
        hasNext,
        dto -> { // sortVal extractor
          return switch (sortField) {
            case "endedAt" -> dto.endedAt();
            case "status"  -> dto.status();
            default        -> dto.startedAt();
          };
        },
        BackupDto::id // id extractor
    );
  }

  /**
   * 최신 완료(COMPLETED) 1건. 없으면 비즈니스 예외 발생.
   */
  public BackupDto findLatestCompleted() {
    return backupRepository.findTopByStatusOrderByEndedAtDesc(BackupStatus.COMPLETED)
        .map(backupMapper::toDto)
        .orElseThrow(() ->
            new BusinessException(ErrorCode.BACKUP_LATEST_NOT_FOUND, "최근 완료 백업이 없습니다."));
  }

  /**
   * 주어진 상태의 최신 1건. 상태값 잘못되면 INVALID_ARGUMENT, 없으면 BACKUP_LATEST_NOT_FOUND.
   */
  public BackupDto findLatestByStatusOrThrow(String statusName) {
    final BackupStatus status;
    try {
      status = BackupStatus.valueOf(statusName.toUpperCase());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "유효하지 않은 상태값: " + statusName);
    }
    return backupRepository.findTopByStatusOrderByEndedAtDesc(status)
        .map(backupMapper::toDto)
        .orElseThrow(() ->
            new BusinessException(ErrorCode.BACKUP_LATEST_NOT_FOUND, "해당 상태의 최근 백업이 없습니다."));
  }

  // --- helpers ---

  private String normalizeSortField(String f) {
    if (Objects.equals(f, "endedAt")) return "endedAt";
    if (Objects.equals(f, "status"))  return "status";
    return "startedAt"; // default
  }

  private boolean hasText(String s) {
    return s != null && !s.isBlank();
  }
}