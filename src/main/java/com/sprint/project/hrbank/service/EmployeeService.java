package com.sprint.project.hrbank.service;
import com.sprint.project.hrbank.dto.employee.EmployeeCreateRequest;
import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.dto.employee.EmployeeUpdateRequest;
import com.sprint.project.hrbank.entity.Department;
import com.sprint.project.hrbank.entity.Employee;
import com.sprint.project.hrbank.entity.File;
import com.sprint.project.hrbank.mapper.EmployeeMapper;
import com.sprint.project.hrbank.repository.DepartmentRepository;
import com.sprint.project.hrbank.repository.EmployeeRepository;
import com.sprint.project.hrbank.repository.FileRepository;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

// 수정사항 반영했습니다!

public class EmployeeService {

  private final EmployeeRepository employeeRepository; // employee db 관리자
  private final DepartmentRepository departmentRepository; // department db 관리자
  private final EmployeeMapper employeeMapper; // employee -> employeeDto로 변환

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

  @Transactional
  public EmployeeDto update(Long employeeId, EmployeeUpdateRequest) {
    // 1. ID로 수정할 직원 엔티티 조회
    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + employeeId));

    // 2. DTO에 담겨온 ID로 연관 엔티티(부서, 프로필 이미지) 조회
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException("Department not found with id: " + request.departmentId()));

    File profileImage = (request.profileImageId() != null) ? fileRepository.findById(request.profileImageId())
        .orElseThrow(() -> new NoSuchElementException("File not found with id: " + request.profileImageId())) : null;

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
}
