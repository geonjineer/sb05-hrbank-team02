package com.sprint.project.hrbank.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.entity.QDepartment;
import com.sprint.project.hrbank.entity.QEmployee;
import com.sprint.project.hrbank.repository.EmployeeQueryRepository;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeQueryRepositoryImpl implements EmployeeQueryRepository {

  private final JPAQueryFactory queryFactory;

  private static final QEmployee e = QEmployee.employee;
  private static final QDepartment d = QDepartment.department;

  @Override
  public List<Employee> search(EmployeeSearchRequest request,
      int sizePlusOne,
      String sortField,
      boolean asc,
      String lastSortVal,
      Long lastId) {

    BooleanBuilder where = new BooleanBuilder();

    // 부분 일치
    if (hasText(request.nameOrEmail())) {
      where.and(
          e.name.containsIgnoreCase(request.nameOrEmail())
              .or(e.email.containsIgnoreCase(request.nameOrEmail()))
      );
    }
    if (hasText(request.employeeNumber())) {
      where.and(e.employeeNumber.containsIgnoreCase(request.employeeNumber()));
    }
    if (hasText(request.departmentName())) {
      where.and(e.department.name.containsIgnoreCase(request.departmentName()));
    }
    if (hasText(request.position())) {
      where.and(e.position.containsIgnoreCase(request.position()));
    }

    // 범위/완전일치
    if (request.hireDateFrom() != null) {
      where.and(e.hireDate.goe(request.hireDateFrom()));
    }
    if (request.hireDateTo() != null) {
      where.and(e.hireDate.loe(request.hireDateTo()));
    }
    if (request.status() != null) {
      where.and(e.status.eq(request.status()));
    }

    // 커서(정렬키 + id) 조건
    if (lastId != null) {
      where.and(buildCursorPredicate(sortField, asc, lastSortVal, lastId));
    }

    // 정렬: sortField 1차 + id 2차
    OrderSpecifier<?> primary = buildPrimaryOrder(sortField, asc);
    OrderSpecifier<Long> tie = asc ? e.id.asc() : e.id.desc();

    // fetch join + size+1
    return queryFactory.selectFrom(e)
        .leftJoin(e.department, d).fetchJoin()
        .where(where)
        .orderBy(primary, tie)
        .limit(sizePlusOne)
        .fetch();
  }

  private OrderSpecifier<?> buildPrimaryOrder(String sortField, boolean asc) {
    return switch (sortField) {
      case "employeeNumber" -> asc ? e.employeeNumber.asc() : e.employeeNumber.desc();
      case "hireDate" -> asc ? e.hireDate.asc() : e.hireDate.desc();
      default -> asc ? e.name.asc() : e.name.desc();
    };
  }

  private Predicate buildCursorPredicate(String sortField, boolean asc,
      @Nullable String lastSortVal, @Nullable Long lastId) {
    return switch (sortField) {
      case "name" -> {
        String value = nullToEmpty(lastSortVal);
        BooleanExpression cmp = asc ? e.name.gt(value) : e.name.lt(value);
        BooleanExpression eq = e.name.eq(value);
        BooleanExpression idc = asc ? e.id.gt(lastId) : e.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      case "employeeNumber" -> {
        String value = nullToEmpty(lastSortVal);
        BooleanExpression cmp = asc ? e.employeeNumber.gt(value) : e.employeeNumber.lt(value);
        BooleanExpression eq = e.employeeNumber.eq(value);
        BooleanExpression idc = asc ? e.id.gt(lastId) : e.id.lt(lastId);
        yield cmp.or(eq.and(idc));
      }
      case "hireDate" -> {
        LocalDate base = (lastSortVal == null || lastSortVal.isBlank())
            ? LocalDate.of(1970, 1, 1)
            : LocalDate.parse(lastSortVal);
        BooleanExpression cmp = asc ? e.hireDate.gt(base) : e.hireDate.lt(base);
        BooleanExpression eq = e.hireDate.loe(base);
        BooleanExpression idc = asc ? e.id.gt(lastId) : e.id.lt(lastId);
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
