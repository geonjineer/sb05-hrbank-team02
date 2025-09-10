package com.sprint.project.hrbank.specification;

import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.entity.EmployeeStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecs {

  private EmployeeSpecs() {
  }

  private static String like(String value) {
    return "%" + value.trim().toLowerCase() + "%";
  }

  public static Specification<Employee> fetchDepartment() {
    return (r, cq, cb) -> {
      r.fetch("department", JoinType.LEFT);
      assert cq != null;
      cq.distinct(true);
      return cb.conjunction();
    };
  }

  public static Specification<Employee> nameOrEmailContains(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    return (r, cq, cb)
        -> cb.or(
        cb.like(cb.lower(r.get("name")), like(value)),
        cb.like(cb.lower(r.get("email")), like(value))
    );
  }

  public static Specification<Employee> employeeNumberContains(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    return (r, cq, cb)
        -> cb.like(cb.lower(r.get("employeeNumber")), like(value)
    );
  }

  public static Specification<Employee> departmentNameContains(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    return (r, cq, cb) -> {
      Join<Object, Object> dept = r.join("department", JoinType.LEFT);
      return cb.like(cb.lower(dept.get("name")), like(value));
    };
  }

  public static Specification<Employee> positionContains(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    return (r, cq, cb)
        -> cb.like(cb.lower(r.get("position")), like(value));
  }

  public static Specification<Employee> hireDateFrom(LocalDate from) {
    if (from == null) {
      return null;
    }
    return (r, cq, cb)
        -> cb.greaterThanOrEqualTo(r.get("hireDate"), from);
  }

  public static Specification<Employee> hireDateTo(LocalDate to) {
    if (to == null) {
      return null;
    }
    return (r, cq, cb)
        -> cb.lessThanOrEqualTo(r.get("hireDate"), to);
  }

  public static Specification<Employee> statusEquals(EmployeeStatus status) {
    if (status == null) {
      return null;
    }
    return (r, cq, cb)
        -> cb.equal(r.get("status"), status);
  }
}
