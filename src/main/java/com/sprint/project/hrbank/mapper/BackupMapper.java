package com.sprint.project.hrbank.mapper;

import com.sprint.project.hrbank.dto.backup.BackupDto;
import com.sprint.project.hrbank.entity.Backup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  @Mapping(source = "file.id", target = "fileId")
  @Mapping(target = "startedAt", expression = "java(entity.getStartedAt()==null?null:entity.getStartedAt().toString())")
  @Mapping(target = "endedAt", expression = "java(entity.getEndedAt()==null?null:entity.getEndedAt().toString())")
  @Mapping(target = "status", expression = "java(entity.getStatus()==null?null:entity.getStatus().name())")
  BackupDto toDto(Backup entity);
}