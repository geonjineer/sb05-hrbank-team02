package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.department.DepartmentDto;
import com.sprint.project.hrbank.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  DepartmentDto toDepartmentDto(Department department);
}
