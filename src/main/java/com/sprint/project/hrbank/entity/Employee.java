package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(updatable = false, nullable = false)
  private String employeeNumber;

  @Column(nullable = false)
  private LocalDate hireDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeStatus status;

  @Column(nullable = false)
  private String position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_image_id")
  private File profileImage;

  public Employee(String name, String email, LocalDate hireDate,
      String position, Department department, File profileImage) {
    this.name = name;
    this.email = email;
    this.hireDate = hireDate;
    this.position = position;
    this.department = department;
    this.profileImage = profileImage;

    this.status = EmployeeStatus.ACTIVE;
  }

  public boolean update(String name, String email, LocalDate hireDate, String position,
      Department department, File profileImage) {
    boolean changed = false;

    if (this.name != null && !name.equals(this.name)) {
      this.name = name;
      changed = true;
    }
    if (this.email != null && !email.equals(this.email)) {
      this.email = email;
      changed = true;
    }
    if (this.hireDate != null && !hireDate.equals(this.hireDate)) {
      this.hireDate = hireDate;
      changed = true;
    }
    if (this.position != null && !position.equals(this.position)) {
      this.position = position;
      changed = true;
    }
    if (this.department != department) {
      this.department = department;
      changed = true;
    }
    if (this.profileImage != profileImage) {
      this.profileImage = profileImage;
      changed = true;
    }

    return changed;
  }
}
