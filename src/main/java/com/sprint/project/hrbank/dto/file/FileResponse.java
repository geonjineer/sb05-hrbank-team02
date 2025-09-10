package com.sprint.project.hrbank.dto.file;

public record FileResponse(
    Long id,
    String fileName,
    String contentType,
    Long size
) {

}
