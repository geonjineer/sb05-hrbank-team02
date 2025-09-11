package com.sprint.project.hrbank.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogCreateRequest;
import com.sprint.project.hrbank.dto.changeLog.ChangeLogDiffCreate;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.entity.ChangeLogType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangeLogCreateRequestMapper {

  ObjectMapper objectMapper = new ObjectMapper();

  public ChangeLogCreateRequest forCreate(EmployeeDto after, String memo, String ip) {
    return ChangeLogCreateRequest.builder()
        .type(ChangeLogType.CREATED)
        .employeeNumber(after.employeeNumber())
        .memo(memo)
        .ipAddress(ip)
        .diffs(List.of(
            diff("name", null, after.name()),
            diff("email", null, normalizeEmail(after.email())),
            diff("departmentId", null, after.departmentId()),
            diff("position", null, after.position()),
            diff("hireDate", null, after.hireDate()),
            diff("status", null, after.status()),
            diff("profileImageId", null, after.profileImageId())
        ))
        .at(Instant.now())
        .build();
  }

  public ChangeLogCreateRequest forUpdate(EmployeeDto before, EmployeeDto after, String memo,
      String ip) {
    List<ChangeLogDiffCreate> diffs = new ArrayList<>();
    addIfChanged(diffs, "name", before.name(), after.name());
    addIfChanged(diffs, "email", normalizeEmail(before.email()), normalizeEmail(after.email()));
    addIfChanged(diffs, "departmentId", before.departmentId(), after.departmentId());
    addIfChanged(diffs, "position", before.position(), after.position());
    addIfChanged(diffs, "hireDate", before.hireDate(), after.hireDate());
    addIfChanged(diffs, "status", before.status(), after.status());
    addIfChanged(diffs, "profileImageId",
        before.profileImageId() == null ? null : before.profileImageId(),
        after.profileImageId() == null ? null : after.profileImageId());

    return ChangeLogCreateRequest.builder()
        .type(ChangeLogType.UPDATED)
        .employeeNumber(after.employeeNumber())
        .memo(memo)
        .ipAddress(ip)
        .diffs(diffs)
        .at(Instant.now())
        .build();
  }

  public ChangeLogCreateRequest forDelete(EmployeeDto before, String ip) {
    return ChangeLogCreateRequest.builder()
        .type(ChangeLogType.DELETED)
        .employeeNumber(before.employeeNumber())
        .memo(null)
        .ipAddress(ip)
        .diffs(List.of(
            diff("name", before.name(), null),
            diff("email", before.email(), null),
            diff("departmentId", before.departmentId(), null),
            diff("position", before.position(), null),
            diff("hireDate", before.hireDate(), null),
            diff("status", before.status(), null),
            diff("profileImageId", before.profileImageId(), null)
        )) // 보통 삭제는 상세 diff 생략
        .at(Instant.now())
        .build();
  }

  // helper

  private void addIfChanged(List<ChangeLogDiffCreate> diffs, String property, Object before,
      Object after) {
    if (!same(before, after)) {
      diffs.add(diff(property, before, after));
    }
  }

  private boolean same(Object before, Object after) {
    return Objects.equals(before, after);
  }

  private ChangeLogDiffCreate diff(String property, Object before, Object after) {
    return new ChangeLogDiffCreate(
        property,
        toJson(before),
        toJson(after)
    );
  }

  private JsonNode toJson(Object value) {
    return value == null ? NullNode.getInstance() : objectMapper.valueToTree(value);
  }

  private String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }


}
