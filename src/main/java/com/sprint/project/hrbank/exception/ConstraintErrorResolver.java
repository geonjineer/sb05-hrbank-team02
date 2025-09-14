package com.sprint.project.hrbank.exception;

import com.sprint.project.hrbank.configuration.ErrorMappingProperties;
import java.sql.SQLException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConstraintErrorResolver {

  private final ErrorMappingProperties props;

  public ErrorCode resolve(DataIntegrityViolationException e) {
    // 1) Hibernate ConstraintViolationException에서 제약명
    ConstraintViolationException hce = findCause(e, ConstraintViolationException.class);
    if (hce != null) {
      String name = hce.getConstraintName();
      ErrorCode code = fromConstraint(name);
      if (code != null) {
        return code;
      }

      ErrorCode fallback = fromSqlState(hce.getSQLException());
      if (fallback != null) {
        return fallback;
      }
    }

    // 2) 일반 SQLException에서 SQLState
    SQLException sqlEx = findCause(e, SQLException.class);
    ErrorCode fallback = fromSqlState(sqlEx);
    if (fallback != null) {
      return fallback;
    }

    return props.getDefaultCode();
  }

  private ErrorCode fromConstraint(String name) {
    if (name == null) {
      return null;
    }
    Map<String, ErrorCode> map = props.getConstraintMap();
    return (map != null) ? map.get(name) : null; // 정확히 일치하는 제약명만 매핑
  }

  private ErrorCode fromSqlState(SQLException se) {
    if (se == null) {
      return null;
    }
    String state = null;
    // SQLException을 순회하면서 일치하는 예외 찾기
    for (SQLException cur = se; cur != null && state == null; cur = cur.getNextException()) {
      state = cur.getSQLState();
    }
    if (state == null) {
      return null;
    }
    Map<String, ErrorCode> map = props.getSqlstateMap();
    return (map != null) ? map.get(state) : null;
  }

  private <T extends Throwable> T findCause(Throwable e, Class<T> type) {
    while (e != null && !type.isInstance(e)) {
      e = e.getCause();
    }
    return type.isInstance(e) ? type.cast(e) : null;
  }
}
