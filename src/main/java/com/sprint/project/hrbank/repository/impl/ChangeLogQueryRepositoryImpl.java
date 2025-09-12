package com.sprint.project.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogSearchRequest;
import com.sprint.project.hrbank.entity.ChangeLog;
import com.sprint.project.hrbank.entity.QChangeLog;
import com.sprint.project.hrbank.repository.ChangeLogQueryRepository;
import io.micrometer.common.lang.Nullable;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChangeLogQueryRepositoryImpl implements ChangeLogQueryRepository {

  private final JPAQueryFactory queryFactory;

  private static final QChangeLog c = QChangeLog.changeLog;

  @Override
  public List<ChangeLog> search(ChangeLogSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc, String lastSortValue,
      Long lastId) {

    BooleanBuilder where = new BooleanBuilder();

    // 부분 일치
    if (hasText(request.employeeNumber())) {
      where.and(c.employeeNumber.containsIgnoreCase(request.employeeNumber()));
    }
    if (hasText(request.memo())) {
      where.and(c.memo.containsIgnoreCase(request.memo()));
    }
    if (hasText(request.ipAddress())) {
      where.and(c.ipAddress.containsIgnoreCase(request.ipAddress()));
    }

    // 범위/완전일치
    if (request.atFrom() != null) {
      where.and(c.at.goe(request.atFrom()));
    }
    if (request.atTo() != null) {
      where.and(c.at.loe(request.atTo()));
    }
    if (request.type() != null) {
      where.and(c.type.eq(request.type()));
    }

    if (lastId != null) {
      where.and(buildCursorPredicate(sortField, asc, lastSortValue, lastId));
    }

    OrderSpecifier<?> primary = buildPrimaryOrder(sortField, asc);
    OrderSpecifier<Long> tie = asc ? c.id.asc() : c.id.desc();

    return queryFactory.selectFrom(c)
        .where(where)
        .orderBy(primary, tie)
        .limit(sizePlusOne)
        .fetch();
  }


  private OrderSpecifier<?> buildPrimaryOrder(String sortField, boolean asc) {
    return switch (sortField) {
      case "ipAddress" -> asc ? c.ipAddress.asc() : c.ipAddress.desc();
      default -> asc ? c.at.asc() : c.at.desc();
    };
  }

  private Predicate buildCursorPredicate(String sortField, boolean asc,
      @Nullable String lastSortValue, @Nullable Long lastId) {
    return switch (sortField) {
      case "ipAddress" -> {
        String value = nullToEmpty(lastSortValue);
        BooleanExpression cmp = asc ? c.ipAddress.gt(value) : c.ipAddress.lt(value);
        BooleanExpression eq = c.ipAddress.eq(value);
        BooleanExpression idc = asc ? c.id.gt(lastId) : c.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      case "at" -> {
        Instant base = (lastSortValue == null || lastSortValue.isBlank())
            ? Instant.ofEpochMilli(0)
            : Instant.parse(lastSortValue);
        BooleanExpression cmp = asc ? c.at.gt(base) : c.at.lt(base);
        BooleanExpression eq = c.at.loe(base);
        BooleanExpression idc = asc ? c.id.gt(lastId) : c.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      default -> Expressions.TRUE.isTrue();
    };
  }

  private static boolean hasText(String s) {
    return s != null && !s.isBlank();
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }
}
