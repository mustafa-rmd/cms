package com.thmanyah.services.cms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thmanyah.services.cms.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "User data transfer object")
public class UserDto {

  @Schema(
      description = "Unique identifier of the user",
      example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(description = "Email address of the user", example = "admin@example.com")
  private String email;

  @Schema(
      description = "Role of the user",
      example = "ADMIN",
      allowableValues = {"ADMIN", "EDITOR"})
  private Role role;

  @Schema(description = "Whether the user account is active", example = "true")
  private Boolean isActive;

  @Schema(description = "Date and time when the user was created", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdDate;

  @Schema(
      description = "Date and time when the user was last updated",
      example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedDate;

  @Schema(description = "Email of the user who created this user", example = "admin@example.com")
  private String createdBy;

  @Schema(
      description = "Email of the user who last updated this user",
      example = "admin@example.com")
  private String updatedBy;

  // Constructor for repository projections
  public UserDto(
      UUID id,
      String email,
      Role role,
      Boolean isActive,
      LocalDateTime createdDate,
      LocalDateTime updatedDate,
      String createdBy,
      String updatedBy) {
    this.id = id;
    this.email = email;
    this.role = role;
    this.isActive = isActive;
    this.createdDate = createdDate;
    this.updatedDate = updatedDate;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
  }
}
