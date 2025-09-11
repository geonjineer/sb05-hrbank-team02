package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.changeLog.ChangeLogDto;
import com.sprint.project.hrbank.entity.ChangeLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

  ChangeLogDto toDto(ChangeLog changeLog);
}
