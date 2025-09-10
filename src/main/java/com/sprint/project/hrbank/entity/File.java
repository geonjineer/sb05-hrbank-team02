package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Getter;


@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //@Column의 length = 255이 기본값
  private String fileName;

  @Column(length = 100)
  private String contentType;

  private long size;

  // PK(id)는 제외하고 필요한 필드만 받는 생성자
  public File(String fileName, String contentType, long size) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
  }
}
