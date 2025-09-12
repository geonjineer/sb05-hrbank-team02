package com.sprint.project.hrbank.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ErrorMappingConfig.class)
public class ErrorMappingConfig {

}
