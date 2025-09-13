package com.sprint.project.hrbank.repository.impl;

import static com.sprint.project.hrbank.entity.QBackup.backup;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.dto.backup.BackupSearchRequest;
import com.sprint.project.hrbank.entity.Backup;
import com.sprint.project.hrbank.repository.BackupQueryRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BackupQueryRepositoryImpl implements BackupQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Backup> search(
      BackupSearchRequest request,
      int sizePlusOne,
      boolean asc,
      String sortField,
      String lastSortVal,
      Long lastId
  ) {
    BooleanBuilder where = new BooleanBuilder();

    String worker = request.worker();
    var status = request.status();
    var startedAtFrom = request.startedAtFrom();
    var startedAtTo = request.startedAtTo();

    if (hasText(worker)) {
      where.and(backup.worker.containsIgnoreCase(worker));
    }
    if (status != null) {
      where.and(backup.status.eq(status));
    }

    // --- 기간 필터: booleanTemplate로 우회 ---
    if (startedAtFrom != null) {
      Instant from = toInstantUtc(startedAtFrom);
      where.and(Expressions.booleanTemplate("{0} >= {1}", backup.startedAt, from));
    }
    if (startedAtTo != null) {
      Instant to = toInstantUtc(startedAtTo);
      where.and(Expressions.booleanTemplate("{0} <= {1}", backup.startedAt, to));
    }

    // --- 커서 프레디케이트 ---
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

  private BooleanExpression buildCursorPredicate(
      String sortField, boolean asc, String lastSortVal, Long lastId
  ) {
    // 타이브레이커: id 비교
    BooleanExpression idCmp = (lastId == null)
        ? Expressions.FALSE.isTrue()
        : (asc
            ? Expressions.booleanTemplate("{0} > {1}", backup.id, lastId)
            : Expressions.booleanTemplate("{0} < {1}", backup.id, lastId)
        );

    // primary key 비교 + tie-breaker(id)
    return switch (sortField) {
      case "endedAt" -> {
        Instant base = parseInstantOrLimit(lastSortVal, asc);
        BooleanExpression keyCmp = asc
            ? Expressions.booleanTemplate("{0} > {1}", backup.endedAt, base)
            : Expressions.booleanTemplate("{0} < {1}", backup.endedAt, base);
        BooleanExpression keyEq  =
            Expressions.booleanTemplate("{0} = {1}", backup.endedAt, base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
      case "status" -> {
        String base = (lastSortVal == null) ? "" : lastSortVal;
        // enum → string 비교 그대로 유지
        BooleanExpression keyCmp = asc
            ? backup.status.stringValue().gt(base)
            : backup.status.stringValue().lt(base);
        BooleanExpression keyEq  = backup.status.stringValue().eq(base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
      default -> { // startedAt
        Instant base = parseInstantOrLimit(lastSortVal, asc);
        BooleanExpression keyCmp = asc
            ? Expressions.booleanTemplate("{0} > {1}", backup.startedAt, base)
            : Expressions.booleanTemplate("{0} < {1}", backup.startedAt, base);
        BooleanExpression keyEq  =
            Expressions.booleanTemplate("{0} = {1}", backup.startedAt, base);
        yield keyCmp.or(keyEq.and(idCmp));
      }
    };
  }

  // 문자열 → Instant 파싱 실패시 극값으로 보정
  private Instant parseInstantOrLimit(String s, boolean asc) {
    try {
      if (s == null || s.isBlank()) return asc ? Instant.MIN : Instant.MAX;
      return Instant.parse(s);
    } catch (Exception ignore) {
      return asc ? Instant.MIN : Instant.MAX;
    }
  }

  // LocalDateTime/LocalDate/OffsetDateTime 등을 UTC Instant로 변환
  private Instant toInstantUtc(Object temporal) {
    if (temporal instanceof Instant i) return i;
    if (temporal instanceof java.time.OffsetDateTime odt) return odt.toInstant();
    if (temporal instanceof java.time.ZonedDateTime zdt) return zdt.toInstant();
    if (temporal instanceof LocalDateTime ldt) return ldt.toInstant(ZoneOffset.UTC);
    if (temporal instanceof java.time.LocalDate ld) return ld.atStartOfDay().toInstant(ZoneOffset.UTC);
    // 그 외에는 그대로 now/혹은 예외 처리 – 여기선 안전하게 now로 보정
    return Instant.now();
  }

  private static boolean hasText(String s) { return s != null && !s.isBlank(); }
}
