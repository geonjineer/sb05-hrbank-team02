package com.sprint.project.hrbank.repository.impl;

import static com.sprint.project.hrbank.entity.QBackup.backup;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.entity.BackupStatus;
import com.sprint.project.hrbank.repository.BackupQueryRepository;
import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BackupQueryRepositoryImpl implements BackupQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Backup> search(
      String worker,
      BackupStatus status,
      OffsetDateTime startedAtFrom,
      OffsetDateTime startedAtTo,
      int sizePlusOne,
      String sortField,
      boolean asc,
      String lastSortVal,
      Long lastId
  ) {
    BooleanBuilder where = new BooleanBuilder();

    if (hasText(worker)) {
      where.and(backup.worker.containsIgnoreCase(worker));
    }
    if (status != null) {
      where.and(backup.status.eq(status));
    }
    if (startedAtFrom != null) {
      where.and(backup.startedAt.goe(startedAtFrom));
    }
    if (startedAtTo != null) {
      where.and(backup.startedAt.loe(startedAtTo));
    }

    // 커서(정렬키 + id)
    if (lastId != null || hasText(lastSortVal)) {
      where.and(buildCursorPredicate(sortField, asc, lastSortVal, lastId));
    }

    OrderSpecifier<?> primary = buildPrimaryOrder(sortField, asc);
    OrderSpecifier<Long> tie   = asc ? backup.id.asc() : backup.id.desc();

    return queryFactory
        .selectFrom(backup)
        .where(where)
        .orderBy(primary, tie)
        .limit(sizePlusOne)
        .fetch();
  }

  private OrderSpecifier<?> buildPrimaryOrder(String sortField, boolean asc) {
    return switch (sortField) {
      case "endedAt"  -> asc ? backup.endedAt.asc()  : backup.endedAt.desc();
      case "status"   -> asc ? backup.status.asc()   : backup.status.desc();
      default         -> asc ? backup.startedAt.asc(): backup.startedAt.desc();
    };
  }

  /**
   * DESC: (key < last) or (key == last and id < lastId)
   * ASC : (key > last) or (key == last and id > lastId)
   */
  private BooleanExpression buildCursorPredicate(
      String sortField, boolean asc,
      @Nullable String lastSortVal,
      @Nullable Long lastId
  ) {
    BooleanExpression idCmp = (lastId == null)
        ? Expressions.FALSE.isTrue()   // id 미지정 시 tie-breaker 비활성
        : (asc ? backup.id.gt(lastId) : backup.id.lt(lastId));

    return switch (sortField) {
      case "endedAt" -> {
        OffsetDateTime base = parseOffsetDateTimeOrLimit(lastSortVal, asc);
        BooleanExpression keyCmp = asc ? backup.endedAt.gt(base) : backup.endedAt.lt(base);
        BooleanExpression keyEq  = backup.endedAt.eq(base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
      case "status" -> {
        String base = (lastSortVal == null) ? "" : lastSortVal;
        BooleanExpression keyCmp = asc
            ? backup.status.stringValue().gt(base)
            : backup.status.stringValue().lt(base);
        BooleanExpression keyEq  = backup.status.stringValue().eq(base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
      case "startedAt" -> {
        OffsetDateTime base = parseOffsetDateTimeOrLimit(lastSortVal, asc);
        BooleanExpression keyCmp = asc ? backup.startedAt.gt(base) : backup.startedAt.lt(base);
        BooleanExpression keyEq  = backup.startedAt.eq(base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
      default -> Expressions.TRUE.isTrue(); // 알 수 없는 필드 → 필터 미적용
    };
  }

  private OffsetDateTime parseOffsetDateTimeOrLimit(String s, boolean asc) {
    try {
      if (s == null || s.isBlank()) return asc ? OffsetDateTime.MIN : OffsetDateTime.MAX;
      return OffsetDateTime.parse(s); // ISO-8601 기대
    } catch (Exception ignore) {
      return asc ? OffsetDateTime.MIN : OffsetDateTime.MAX;
    }
  }

  private static boolean hasText(String s) {
    return s != null && !s.isBlank();
  }
}