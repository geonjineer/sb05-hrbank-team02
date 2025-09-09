package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.employee.EmployeeDto;
import com.sprint.project.hrbank.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "departmentId", source = "department.id")
  @Mapping(target = "departmentName", source = "department.name")
  EmployeeDto toDto(Employee employee);
}
