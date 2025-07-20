package com.thmanyah.services.cms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Schema(description = "User email", example = "admin@thmanyah.io")
  private String email;

  @NotBlank(message = "Password is required")
  @Schema(description = "User password", example = "AdminPassword123!")
  private String password;
}
