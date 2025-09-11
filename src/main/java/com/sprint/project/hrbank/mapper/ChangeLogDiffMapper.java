package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.changeLog.DiffDto;
import com.sprint.project.hrbank.entity.ChangeLogDiff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeLogDiffMapper {

  DiffDto toDto(ChangeLogDiff changeLogDiff);
}
