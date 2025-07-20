package com.thmanyah.services.cms.model.dto;

import com.thmanyah.services.cms.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data transfer object for creating a new user")
public class UserCreateDto {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  @Schema(description = "Email address of the user", example = "editor@example.com")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
  @Schema(description = "Password for the user (will be hashed)", example = "SecurePassword123!")
  private String password;

  @NotNull(message = "Role is required")
  @Schema(
      description = "Role of the user",
      example = "EDITOR",
      allowableValues = {"ADMIN", "EDITOR"})
  private Role role;
}
