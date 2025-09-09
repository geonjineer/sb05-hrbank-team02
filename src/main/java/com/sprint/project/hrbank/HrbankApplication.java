package com.sprint.project.hrbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class HrbankApplication {

  public static void main(String[] args) {
    SpringApplication.run(HrbankApplication.class, args);
  }

}
