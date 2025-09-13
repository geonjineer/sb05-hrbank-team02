package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.exception.BusinessException;
import com.sprint.project.hrbank.exception.ErrorCode;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.mapper.DepartmentMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper;
  private final CursorCodec cursorCodec;
  private final CursorPageAssembler cursorPageAssembler;

  @Transactional
  public DepartmentDto create(DepartmentCreateRequest request) {
    validateUniqueName(request.name());

    String name = request.name();
    String description = request.description();
    LocalDate establishedDate = request.establishedDate();

    Department department = Department.builder()
        .name(name).description(description).establishedDate(establishedDate).build();

    long employeeCount = 0L;
    departmentRepository.save(department);

    return departmentMapper.toDepartmentDto(department, employeeCount);
  }


  @Transactional(readOnly = true)
  public DepartmentDto find(Long id) {
    Department department = validateId(id);
    Long employeeCount = employeeRepository.countByDepartment(department);
    return departmentMapper.toDepartmentDto(department, employeeCount);
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<DepartmentDto> findAll(DepartmentSearchRequest request) {
    Integer size = request.size();
    String sortField = request.sortField();

    boolean asc = !"desc".equalsIgnoreCase(request.sortDirection());

    String lastSortVal = null;
    Long lastId = null;

    if (request.cursor() != null && !request.cursor().isBlank()) {
      CursorPayload c = cursorCodec.decode(request.cursor(), CursorPayload.class);
      lastSortVal = c.sortVal();
      lastId = c.id();
    } else if (request.idAfter() != null) {
      Long idAfter = request.idAfter();
      Department department = validateId(idAfter);

      lastSortVal = switch (sortField) {
        case "name" -> department.getName();
        case "establishedDate" -> department.getEstablishedDate() == null ?
            "" : department.getEstablishedDate().toString();
        default -> "";
      };
      lastId = department.getId();
    }

    List<DepartmentDto> content = departmentRepository
        .search(request, size + 1, sortField, asc, lastSortVal, lastId).stream()
        .map(dept -> departmentMapper.toDepartmentDto(dept,
            employeeRepository.countByDepartment(dept)))
        .toList();

    boolean hasNext = content.size() > size;
    if (hasNext) {
      content = content.subList(0, size);
    }

    Function<DepartmentDto, String> sortValFn = d -> switch (sortField) {
      case "name" -> d.name() == null ? "" : d.name();
      case "establishedDate" -> d.establishedDate() == null ? "" : d.establishedDate().toString();
      default -> "";
    };

    return cursorPageAssembler.assemble(
        content,
        size,
        hasNext,
        sortValFn,
        DepartmentDto::id,
        CursorPageResponse<DepartmentDto>::new
    );

  }

  @Transactional
  public DepartmentDto update(Long id, DepartmentUpdateRequest request) {
    Department department = validateId(id);
    validateUniqueName(request.name());

    department.setName(request.name());
    department.setDescription(request.description());
    department.setEstablishedDate(request.establishedDate());

    return departmentMapper.toDepartmentDto(department,
        employeeRepository.countByDepartment(department));
  }

  @Transactional
  public void delete(Long id) {
    Department department = validateId(id);

    if (employeeRepository.existsByDepartment(department)) {
      throw new BusinessException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
    }

    // 3. 부서 삭제
    departmentRepository.deleteById(id);
  }

  private Department validateId(Long id) {
    return departmentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Department not found with id {}", id);
          return new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "departmentId");
        });
  }

  private void validateUniqueName(String name) {
    if (employeeRepository.existsByName(name)) {
      log.warn("Duplicate name found for department {}", name);
      throw new BusinessException(ErrorCode.DEPARTMENT_NAME_DUPLICATE, "departmentName");
    }
  }
}
