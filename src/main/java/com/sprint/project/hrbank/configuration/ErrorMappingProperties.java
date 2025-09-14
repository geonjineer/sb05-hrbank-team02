package com.sprint.project.hrbank.configuration;

import com.sprint.project.hrbank.exception.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.error")
public class ErrorMappingProperties {

  private Map<String, ErrorCode> constraintMap = new HashMap<>();
  private Map<String, ErrorCode> sqlstateMap = new HashMap<>();
  private ErrorCode defaultCode = ErrorCode.INTERNAL_ERROR;

}

