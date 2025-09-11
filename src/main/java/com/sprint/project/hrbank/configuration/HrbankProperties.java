package com.sprint.project.hrbank.configuration;


import com.sprint.project.hrbank.entity.Backup;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.spi.support.StorageAccess;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hrbank")
@Getter
public class HrbankProperties {

  private final Storage storage = new Storage();
  private final Backup backup = new Backup();

  @Getter
  @Setter
  public static class Storage {
    //파일 저장 루트 디렉토리 경로
    private String root = "./storage";

  }

  @Getter
  @Setter
  public static class Backup {
    //스케쥴러 실행 cron 표현식
    private String cron = "0 0 * * * *";

    //스케쥴러 실행 타임존 표현식
    private String timezone = "Asia/Seoul";
  }


}
