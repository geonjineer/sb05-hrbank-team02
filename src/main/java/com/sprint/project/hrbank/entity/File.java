package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "files")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //@Column의 length = 255이 기본값
  @Column(name = "file_name")
  private String fileName;

  @Column(name = "content_type", length = 100)
  private String contentType;

  @Column()
  private long size;
}
