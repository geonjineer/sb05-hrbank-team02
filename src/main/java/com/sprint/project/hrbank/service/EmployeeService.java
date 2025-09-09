package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.Specification.EmployeeSpecs;
import com.sprint.project.hrbank.dto.employee.CursorPageResponse;
import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.mapper.EmployeeMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;
  private final CursorCodec cursorCodec;
  private final CursorPageAssembler cursorPageAssembler;

  private static final Set<String> ALLOWED_SORT = Set.of("name", "employeeNumber", "hireDate");

  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request) {
    String name = request.name();
    String email = request.email();
    String position = request.position();
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("Department not found"));
    LocalDate hireDate = request.hireDate();

    Employee employee = new Employee(name, email, hireDate, position, department, null);

    employeeRepository.save(employee);
    return employeeMapper.toDto(employee);
  }

  @Transactional(readOnly = true)
  public EmployeeDto findById(Long id) {
    return employeeMapper.toDto(employeeRepository.findById(id).orElseThrow(
        () -> new NoSuchElementException("Employee not found with id: " + id)
    ));
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<EmployeeDto> find(EmployeeSearchRequest request) {
    int size = (request.size() == null || request.size() <= 0) ? 10 : request.size();
    String sortField = ALLOWED_SORT.contains(request.sortField()) ? request.sortField() : "name";
    boolean asc = !"desc".equalsIgnoreCase(request.sortDirection());

    if(request.hireDateFrom() != null && request.hireDateTo() != null
    && request.hireDateFrom().isAfter(request.hireDateTo())) {
      throw new IllegalArgumentException("hireDateFrom이 hireDateTo보다 이후일 수 없습니다.");
    }

    Specification<Employee> spec = Specification.<Employee>unrestricted()
        .and(EmployeeSpecs.nameOrEmailContains(request.nameOrEmail()))
        .and(EmployeeSpecs.employeeNumberContains(request.employeeNumber()))
        .and(EmployeeSpecs.departmentNameContains(request.departmentName()))
        .and(EmployeeSpecs.fetchDepartment())
        .and(EmployeeSpecs.positionContains(request.position()))
        .and(EmployeeSpecs.hireDateFrom(request.hireDateFrom()))
        .and(EmployeeSpecs.hireDateTo(request.hireDateTo()))
        .and(EmployeeSpecs.statusEquals(request.status()));

    // 커서/아이디 이후 조건
    if (request.cursor() != null && !request.cursor().isBlank()) {
      CursorPayload c = cursorCodec.decode(request.cursor(), CursorPayload.class);
      spec = spec.and(cursorPredicate(sortField, asc, c.sortVal(), c.id()));
    } else if (request.idAfter() != null) {
      // idAfter는 id 기준으로만 진행 (정렬 상관없이 “그 다음” 의미)
      Long idAfter = request.idAfter();
      Employee last = employeeRepository.findById(idAfter)
          .orElseThrow(() -> new NoSuchElementException("Employee not found: " + idAfter));

      String lastSortVal = switch (sortField) {
        case "name" -> last.getName();
        case "employeeNumber" -> last.getEmployeeNumber();
        case "hireDate" -> last.getHireDate() == null ? "" : last.getHireDate().toString();
        default -> "";
      };
    spec = spec.and(cursorPredicate(sortField, asc, lastSortVal, last.getId()));
    }

    // 정렬: sortField 1차 + id 2차
    Sort sort = asc
        ? Sort.by(sortField).ascending().and(Sort.by("id").ascending())
        : Sort.by(sortField).descending().and(Sort.by("id").descending());

    // size+1로 hasNext 판단
    Pageable pageable = PageRequest.of(0, size + 1, sort);
    List<Employee> rows = employeeRepository.findAll(spec, pageable).getContent();

    boolean hasNext = rows.size() > size;
    if (hasNext) {
      rows = rows.subList(0, size);
    }


    Function<EmployeeDto, String> sortValFn = (e)
        -> switch (sortField) {
      case "name" -> e.name();
      case "employeeNumber" -> e.employeeNumber();
      case "hireDate" -> e.hireDate() == null ? "" : e.hireDate().toString();
      default -> "";
    };

    List<EmployeeDto> content = rows.stream()
        .map(employeeMapper::toDto)
        .toList();

    return cursorPageAssembler.assemble(
        content,
        size,
        hasNext,
        sortValFn,
        EmployeeDto::id,
        CursorPageResponse<EmployeeDto>::new
    );

  }

  private Specification<Employee> cursorPredicate(
      String sortField, boolean asc, String lastSortVal, Long lastId
  ) {
    return (r, cq, cb) -> {
      // null 안전 비교: 문자열은 coalesce(sf, ''), 날짜는 coalesce(sf, '1970-01-01')
      switch (sortField) {
        case "name", "employeeNumber" -> {
          Path<String> path = r.get(sortField);
          Expression<String> sfStr = cb.coalesce(path, cb.literal(""));
          String val = lastSortVal == null ? "" : lastSortVal;
          Predicate cmp = asc ? cb.greaterThan(sfStr, val) : cb.lessThan(sfStr, val);
          Predicate eq = cb.equal(sfStr, val);
          Predicate idCmp =
              asc ? cb.greaterThan(r.get("id"), lastId) : cb.lessThan(r.get("id"), lastId);
          return cb.or(cmp, cb.and(eq, idCmp));
        }
        case "hireDate" -> {
          Path<LocalDate> path = r.get("hireDate");
          Expression<LocalDate> sfd = cb.coalesce(path, cb.literal(LocalDate.of(1970, 1, 1)));
          LocalDate val = (lastSortVal == null || lastSortVal.isBlank() ? LocalDate.of(1970, 1, 1)
              : LocalDate.parse(lastSortVal));
          Predicate cmp = asc ? cb.greaterThan(sfd, val) : cb.lessThan(sfd, val);
          Predicate eq = cb.equal(sfd, val);
          Predicate idCmp =
              asc ? cb.greaterThan(r.get("id"), lastId) : cb.lessThan(r.get("id"), lastId);
          return cb.or(cmp, cb.and(eq, idCmp));
        }
        default -> {
          return cb.conjunction();
        }
      }
    };
  }
}
