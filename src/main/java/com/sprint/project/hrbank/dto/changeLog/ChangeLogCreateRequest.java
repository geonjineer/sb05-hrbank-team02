package com.sprint.project.hrbank.dto.changeLog;

import com.sprint.project.hrbank.entity.ChangeLogType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Builder
public record ChangeLogCreateRequest(

    ChangeLogType type,

    @NotBlank(message = "사원 번호는 필수입니다.")
    String employeeNumber,

    List<ChangeLogDiffCreate> diffs,

    @NotBlank(message = "IP는 필수입니다.")
    String ipAddress,

    @DateTimeFormat(iso = ISO.DATE_TIME)
    @NotBlank(message = "시간은 필수입니다.")
    Instant at,

    @Max(value = 500, message = "메모는 최대 500자까지 가능합니다.")
    String memo
) {

}
