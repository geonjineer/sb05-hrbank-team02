package com.sprint.project.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@Getter
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(updatable = false, nullable = false, insertable = false)
  @Generated(event = EventType.INSERT)
  private String employeeNumber;

  @Column(nullable = false)
  private LocalDate hireDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EmployeeStatus status;

  @Column(nullable = false)
  private String position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
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

  public void update(String name, String email, LocalDate hireDate,
      EmployeeStatus status, String position, Department department, File profileImage) {

    if (name != null && !name.equals(this.name)) {
      this.name = name;
    }
    if (email != null && !email.equals(this.email)) {
      this.email = email;
    }
    if (hireDate != null && !hireDate.equals(this.hireDate)) {
      this.hireDate = hireDate;
    }
    if (status != null && !status.equals(this.status)) {
      this.status = status;
    }
    if (position != null && !position.equals(this.position)) {
      this.position = position;
    }
    if (department != null && !department.equals(this.department)) {
      this.department = department;
    }
    if (profileImage != null && !profileImage.equals(this.profileImage)) {
      this.profileImage = profileImage;
    }

  }
}
