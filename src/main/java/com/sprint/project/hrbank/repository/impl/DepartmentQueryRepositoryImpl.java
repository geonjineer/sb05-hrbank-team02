package com.sprint.project.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.QDepartment;
import com.sprint.project.hrbank.repository.DepartmentQueryRepository;
import io.micrometer.common.lang.Nullable;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepartmentQueryRepositoryImpl implements DepartmentQueryRepository {

  private final JPAQueryFactory queryFactory;
  private static final QDepartment d = QDepartment.department;

  @Override
  public List<Department> search(DepartmentSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc,
      @Nullable String lastSortVal,
      @Nullable Long lastId) {

    BooleanBuilder where = new BooleanBuilder();

    // -------- 검색 조건 --------
    // 이름/설명 부분 일치 (keyword)
    if (hasText(request.keyword())) {
      where.and(
          d.name.containsIgnoreCase(request.keyword())
              .or(d.description.containsIgnoreCase(request.keyword()))
      );
    }
    // 이름 개별 조건
    if (hasText(request.name())) {
      where.and(d.name.containsIgnoreCase(request.name()));
    }
    // 설립일(정확 일치) — 필요에 따라 범위검색으로 확장 가능
    if (request.establishedDate() != null) {
      where.and(d.establishedDate.eq(request.establishedDate()));
    }

    // -------- 커서 조건 (정렬키 + id) --------
    if (lastId != null) {
      where.and(buildCursorPredicate(sortField, asc, lastSortVal, lastId));
    }

    // -------- 정렬: 1차 sortField, 2차 id --------
    OrderSpecifier<?> primary = buildPrimaryOrder(sortField, asc);
    OrderSpecifier<Long> tie = asc ? d.id.asc() : d.id.desc();

    return queryFactory.selectFrom(d)
        .where(where)
        .orderBy(primary, tie)
        .limit(sizePlusOne) // size + 1로 호출됨
        .fetch();
  }

  @Override
  public Long searchCount(@Nullable LocalDate establishedDate) {
    // 정책에 맞춰 null 이면 0L 반환 (Employee 쪽 시그니처와 맞춤)
    if (establishedDate == null) return 0L;

    return queryFactory
        .select(d.count())
        .from(d)
        .where(d.establishedDate.loe(establishedDate))
        .fetchOne();
  }

  // ----------------- 내부 헬퍼 -----------------

  private OrderSpecifier<?> buildPrimaryOrder(String sortField, boolean asc) {
    return switch (sortField) {
      case "establishedDate" -> asc ? d.establishedDate.asc() : d.establishedDate.desc();
      case "name"            -> asc ? d.name.asc()            : d.name.desc();
      default                -> asc ? d.name.asc()            : d.name.desc(); // 기본 name
    };
  }

  private Predicate buildCursorPredicate(String sortField,
      boolean asc,
      @Nullable String lastSortVal,
      @Nullable Long lastId) {
    return switch (sortField) {
      case "name" -> {
        String value = nullToEmpty(lastSortVal);
        BooleanExpression cmp = asc ? d.name.gt(value) : d.name.lt(value);
        BooleanExpression eq  = d.name.eq(value);
        BooleanExpression idc = asc ? d.id.gt(lastId) : d.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      case "establishedDate" -> {
        LocalDate base = (lastSortVal == null || lastSortVal.isBlank())
            ? LocalDate.of(1970, 1, 1)
            : LocalDate.parse(lastSortVal);
        BooleanExpression cmp = asc ? d.establishedDate.gt(base) : d.establishedDate.lt(base);
        BooleanExpression eq  = d.establishedDate.eq(base);
        BooleanExpression idc = asc ? d.id.gt(lastId) : d.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      default -> Expressions.TRUE.isTrue(); // 알 수 없는 정렬키면 커서 조건 생략
    };
  }

  private static boolean hasText(String s) {
    return s != null && !s.isBlank();
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }
}
