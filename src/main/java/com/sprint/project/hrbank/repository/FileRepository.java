package com.sprint.project.hrbank.repository;

import com.sprint.project.hrbank.entity.File;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

  List<File> id(Long id);
}
