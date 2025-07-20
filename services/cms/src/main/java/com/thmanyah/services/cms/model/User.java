package com.thmanyah.services.cms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_users_email", columnList = "email"),
      @Index(name = "idx_users_role", columnList = "role"),
      @Index(name = "idx_users_created_date", columnList = "createdDate")
    })
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(nullable = false, length = 255)
  private String password; // BCrypt hashed password

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "updated_date", nullable = false)
  private LocalDateTime updatedDate;

  @Column(name = "created_by", length = 255)
  private String createdBy;

  @Column(name = "updated_by", length = 255)
  private String updatedBy;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @PrePersist
  private void prePersist() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    this.createdDate = now;
    this.updatedDate = now;
    if (this.isActive == null) {
      this.isActive = true;
    }
  }

  @PreUpdate
  private void preUpdate() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    if (this.createdDate == null) {
      this.createdDate = now;
    }
    this.updatedDate = now;
  }
}
