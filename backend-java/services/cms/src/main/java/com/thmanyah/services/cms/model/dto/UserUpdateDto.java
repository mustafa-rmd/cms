package com.thmanyah.services.cms.model.dto;

import com.thmanyah.services.cms.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data transfer object for updating a user")
public class UserUpdateDto {

  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  @Schema(description = "Email address of the user", example = "editor@example.com")
  private String email;

  @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
  @Schema(
      description = "New password for the user (will be hashed)",
      example = "NewSecurePassword123!")
  private String password;

  @Schema(
      description = "Role of the user",
      example = "EDITOR",
      allowableValues = {"ADMIN", "EDITOR"})
  private Role role;

  @Schema(description = "Whether the user account is active", example = "true")
  private Boolean isActive;
}
