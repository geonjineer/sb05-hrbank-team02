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
import com.sprint.project.hrbank.exception.BusinessException;
import com.sprint.project.hrbank.exception.ErrorCode;
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
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;
  private final CursorCodec cursorCodec;
  private final CursorPageAssembler cursorPageAssembler;
  private final FileRepository fileRepository;
  private final ChangeLogService changeLogService;
  private final ChangeLogCreateRequestMapper changeLogCreateRequestMapper;

  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, FileResponse profileResponse) {
    String name = request.name();
    String email = request.email();
    String position = request.position();

    validateUniqueName(name);
    validateUniqueEmail(email);

    File profileImage = profileResponse == null
        ? null
        : validateFileId(profileResponse.id());

    Department department = validateDepartmentId(request.departmentId());
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
    int size = request.size();
    String sortField = request.sortField();
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
      Employee last = validateId(idAfter);

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
    Department department = validateDepartmentId(request.departmentId());

    validateUniqueName(request.name());
    validateUniqueEmail(request.email());

    File profileImage = profileResponse == null
        ? null
        : validateFileId(profileResponse.id());

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

      log.warn("Duplicate employee name: {}", name);
      throw new BusinessException(ErrorCode.EMPLOYEE_NAME_DUPLICATE, "employeeName");
    }
  }

  private void validateUniqueEmail(String email) {
    if (employeeRepository.existsByEmail(email)) {

      log.warn("Duplicate employee email: {}", email);
      throw new BusinessException(ErrorCode.EMPLOYEE_EMAIL_DUPLICATE, "email");
    }
  }

  private Employee validateId(Long employeeId) {
    return employeeRepository.findById(employeeId)
        .orElseThrow(() -> {
          log.warn("Employee not found with id: {}", employeeId);
          return new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND, "employeeId");
        });
  }

  private Department validateDepartmentId(Long departmentId) {
    return departmentRepository.findById(departmentId)
        .orElseThrow(() -> {
          log.warn("Department not found with id: {}", departmentId);
          return new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "departmentId");
        });
  }

  private File validateFileId(Long profileImageId) {
    return fileRepository.findById(profileImageId)
        .orElseThrow(() -> {
          log.warn("Profile image not found with id: {}", profileImageId);
          return new BusinessException(ErrorCode.PROFILE_IMAGE_NOT_FOUND, "profileImageId");
        });
  }
}
