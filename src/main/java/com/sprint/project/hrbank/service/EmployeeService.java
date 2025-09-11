package com.sprint.project.hrbank.service;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogCreateRequest;
import com.sprint.project.hrbank.dto.common.CursorPageResponse;
import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeSearchRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeUpdateRequest;
import com.sprint.project.hrbank.dto.file.FileResponse;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.mapper.ChangeLogCreateRequestMapper;
import com.sprint.project.hrbank.mapper.CursorCodec;
import com.sprint.project.hrbank.mapper.CursorCodec.CursorPayload;
import com.sprint.project.hrbank.mapper.CursorPageAssembler;
import com.sprint.project.hrbank.mapper.EmployeeMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import com.sprint.project.hrbank.repository.FileRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
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
  private final FileRepository fileRepository;
  private final ChangeLogService changeLogService;
  private final ChangeLogCreateRequestMapper changeLogCreateRequestMapper;

  private static final Set<String> ALLOWED_SORT = Set.of("name", "employeeNumber", "hireDate");

  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, FileResponse profileResponse) {
    String name = request.name();
    String email = request.email();
    String position = request.position();

    validateUniqueName(name);
    validateUniqueEmail(email);

    File profileImage = profileResponse == null
        ? null
        : fileRepository.findById(profileResponse.id())
            .orElseThrow(() -> new NoSuchElementException(
                "Profile image not found: " + profileResponse.id()));

    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(
            () -> new NoSuchElementException("Department not found: " + request.departmentId()));
    LocalDate hireDate = request.hireDate();

    Employee employee = new Employee(name, email, hireDate, position, department, profileImage);

    employeeRepository.save(employee);
    return employeeMapper.toDto(employee);
  }

  @Transactional
  public EmployeeDto createWithLog(EmployeeCreateRequest request, FileResponse profileResponse,
      String ip) {
    EmployeeDto employeeDto = create(request, profileResponse);
    ChangeLogCreateRequest logRequest = changeLogCreateRequestMapper.forCreate(employeeDto,
        request.memo(), ip);
    changeLogService.create(logRequest);

    return employeeDto;
  }

  @Transactional(readOnly = true)
  public EmployeeDto findById(Long id) {
    return employeeMapper.toDto(validateId(id));
  }

  @Transactional(readOnly = true)
  public CursorPageResponse<EmployeeDto> find(EmployeeSearchRequest request) {
    int raw = (request.size() == null || request.size() <= 0) ? 10 : request.size();
    int size = Math.min(raw, 100); // 한 페이지에 담을 수 있는 최댓값을 100으로

    if (request.hireDateFrom() != null && request.hireDateTo() != null
        && request.hireDateFrom().isAfter(request.hireDateTo())) {
      throw new IllegalArgumentException("hireDateFrom이 hireDateTo보다 이후일 수 없습니다.");
    }

    // sortField: 정렬 기준으로 name, employeeNumber, hireDate 중 무엇을 쓸 건지 정하고
    // 셋 중 어디에도 해당하지 않으면 name을 기준으로 정렬함
    String sortField = ALLOWED_SORT.contains(request.sortField()) ? request.sortField() : "name";
    // 오름차순, 내림차순 정렬 결정
    boolean asc = !"desc".equalsIgnoreCase(request.sortDirection());

    // 커서/아이디 이후 준비 (lastSortVal, lastId)
    String lastSortVal = null;
    Long lastId = null;

    if (request.cursor() != null && !request.cursor().isBlank()) {
      CursorPayload c = cursorCodec.decode(request.cursor(), CursorPayload.class);
      lastSortVal = c.sortVal();
      lastId = c.id();
    } else if (request.idAfter() != null) {
      // idAfter는 id 기준으로만 진행 (정렬 상관없이 “그 다음” 의미)
      Long idAfter = request.idAfter();
      Employee last = employeeRepository.findById(idAfter)
          .orElseThrow(() -> new NoSuchElementException("Employee not found: " + idAfter));

      lastSortVal = switch (sortField) {
        case "name" -> last.getName();
        case "employeeNumber" -> last.getEmployeeNumber();
        case "hireDate" -> last.getHireDate() == null ? "" : last.getHireDate().toString();
        default -> "";
      };
      lastId = last.getId();
    }

    // QueryDSL 실행 (size + 1)
    List<Employee> rows = employeeRepository
        .search(request, size + 1, sortField, asc, lastSortVal, lastId);

    boolean hasNext = rows.size() > size;
    if (hasNext) {
      rows = rows.subList(0, size);
    }

    List<EmployeeDto> content = rows.stream()
        .map(employeeMapper::toDto)
        .toList();

    Function<EmployeeDto, String> sortValFn = (e)
        -> switch (sortField) {
      case "name" -> e.name() == null ? "" : e.name();
      case "employeeNumber" -> e.employeeNumber() == null ? "" : e.employeeNumber();
      case "hireDate" -> e.hireDate() == null ? "" : e.hireDate().toString();
      default -> "";
    };

    return cursorPageAssembler.assemble(
        content,
        size,
        hasNext,
        sortValFn,
        EmployeeDto::id,
        CursorPageResponse<EmployeeDto>::new
    );
  }

  @Transactional
  public EmployeeDto update(Long employeeId, EmployeeUpdateRequest request,
      FileResponse profileResponse) {
    // 1. ID로 수정할 직원 엔티티 조회
    Employee employee = validateId(employeeId);

    // 2. DTO에 담겨온 ID로 연관 엔티티(부서, 프로필 이미지) 조회
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException(
            "Department not found with id: " + request.departmentId()));

    validateUniqueName(request.name());
    validateUniqueEmail(request.email());

    File profileImage = profileResponse == null
        ? null
        : fileRepository.findById(profileResponse.id())
            .orElseThrow(() -> new NoSuchElementException(
                "Profile image not found with id: " + profileResponse.id()));

    // 3. 엔티티 값을 DTO 값으로 변경 (더티 체킹 활용)
    employee.update(
        request.name(),
        request.email(),
        request.hireDate(),
        request.position(),
        department,
        profileImage
    );

    // 4. 변경된 엔티티를 DTO로 변환하여 반환
    return employeeMapper.toDto(employee);
  }

  @Transactional
  public EmployeeDto updateWithLog(Long employeeId, EmployeeUpdateRequest request,
      FileResponse profileResponse, String ip) {
    EmployeeDto employeeBefore = employeeMapper.toDto(validateId(employeeId));

    EmployeeDto employeeAfter = update(employeeId, request, profileResponse);

    ChangeLogCreateRequest logRequest = changeLogCreateRequestMapper.forUpdate(employeeBefore,
        employeeAfter,
        request.memo(), ip);

    changeLogService.create(logRequest);

    return employeeBefore;
  }

  @Transactional
  public void deleteWithLog(Long employeeId, String ip) { // 삭제할 직원 id 확인
    EmployeeDto employeeBefore = employeeMapper.toDto(validateId(employeeId));
    ChangeLogCreateRequest logRequest = changeLogCreateRequestMapper.forDelete(employeeBefore, ip);
    changeLogService.create(logRequest);

    employeeRepository.deleteById(employeeId); // 직원 아이디 삭제
  }

  private void validateUniqueName(String name) {
    if (employeeRepository.existsByName(name)) {
      throw new IllegalArgumentException("Employee already exists with name: " + name);
    }
  }

  private void validateUniqueEmail(String email) {
    if (employeeRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Employee already exists with email: " + email);
    }
  }

  private Employee validateId(Long employeeId) {
    return employeeRepository.findById(employeeId)
        .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + employeeId));
  }
}
