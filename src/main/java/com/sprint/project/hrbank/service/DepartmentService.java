package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.department.DepartmentCreateRequest;
import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.dto.department.DepartmentSearchRequest;
import com.sprint.project.hrbank.dto.department.DepartmentUpdateRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.mapper.DepartmentMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final EmployeeRepository employeeRepository;
  private final DepartmentMapper departmentMapper;

  private final CursorCodec cursorCodec;
  private final CursorPageAssembler cursorPageAssembler;
  private static final Set<String> ALLOWED_SORT = Set.of("name", "establishedDate");

  @Transactional
  public DepartmentDto create(DepartmentCreateRequest request) {
    String name = request.name();
    String description = request.description();
    LocalDate establishedDate = request.establishedDate();

    Department department = Department.builder()
        .name(name).description(description).establishedDate(establishedDate).build();

    Long employeeCount = 0L;
    departmentRepository.save(department);

    return departmentMapper.toDepartmentDto(department, employeeCount);
  }


  @Transactional(readOnly = true)
  public DepartmentDto find(Long id) {
     return departmentRepository.findById(id)
        .map(department -> {
          Long employeeCount = employeeRepository.countByDepartment(department);
          return departmentMapper.toDepartmentDto(department, employeeCount);
        }).orElse(null);
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<DepartmentDto> findAll(DepartmentSearchRequest request) {
    int raw = (request.size() == null || request.size() < 1) ? 10 : request.size();
    int size = Math.min(raw, 100);

    String sortField = Optional.ofNullable(request.sortField())
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .filter(ALLOWED_SORT::contains)
        .orElse("establishedDate");

    boolean asc = !"desc".equalsIgnoreCase(request.sortDirection());

    String lastSortVal = null;
    Long lastId = null;

    if (request.cursor() != null && !request.cursor().isBlank()) {
      CursorPayload c = cursorCodec.decode(request.cursor(), CursorPayload.class);
      lastSortVal = c.sortVal();
      lastId = c.id();
    } else if (request.idAfter() != null) {
      Long idAfter = request.idAfter();
      Department department = departmentRepository.findById(idAfter)
          .orElseThrow(() -> new NoSuchElementException("Department with id " + idAfter + " not found"));

      lastSortVal = switch (sortField) {
        case "name" -> department.getName();
        case "establishedDate" -> department.getEstablishedDate() == null ?
            "" : department.getEstablishedDate().toString();
        default -> "";
      };
      lastId = department.getId();
    }

    List<Department> rows = departmentRepository
        .search(request, size + 1, sortField, asc, lastSortVal, lastId);

    boolean hasNext = rows.size() > size;
    if (hasNext) {
      rows = rows.subList(0, size);
    }

    List<DepartmentDto> content = rows.stream()
        .map(dept -> departmentMapper.toDepartmentDto(dept, employeeRepository.countByDepartment(dept)))
        .toList();

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
    Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + id));
    if (!department.getName().equals(request.name()) && departmentRepository.existsByName(request.name())) {
      throw new IllegalArgumentException("Department with name " + request.name() + " already exists.");
    }

    department.setName(request.name());
    department.setDescription(request.description());
    department.setEstablishedDate(request.establishedDate());

    return departmentMapper.toDepartmentDto(department, employeeRepository.countByDepartment(department));
  }

  @Transactional
  public void delete(Long id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new
            NoSuchElementException("Department not found with id: "
            + id));

    if (employeeRepository.existsByDepartment(department)) {
      throw new IllegalArgumentException("Deletion failed: Employees are still assigned to\n" +
              "  this department.");
    }

    // 3. 부서 삭제
    departmentRepository.deleteById(id);
  }
}
