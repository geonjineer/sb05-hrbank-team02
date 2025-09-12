package com.sprint.project.hrbank.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "hrbank")
public class HrbankProperties {

  private final Storage storage = new Storage();
  private final Backup backup = new Backup();

  @Getter
  public static class Storage {
    /**
     * 파일 저장 루트 디렉토리 경로 (기본값: ./storage)
     */
    private String root = "./storage";
    public void setRoot(String root) { this.root = root; }
  }

  @Getter
  public static class Backup {
    /**
     * 스케줄러 실행 cron 표현식 (예: "0 0 * * * *" → 매시 정각)
     */
    private String cron = "0 0 * * * *";
    /**
     * 스케줄러 실행 타임존 (기본: UTC)
     */
    private String timezone = "UTC";

    public void setCron(String cron) { this.cron = cron; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
  }
}