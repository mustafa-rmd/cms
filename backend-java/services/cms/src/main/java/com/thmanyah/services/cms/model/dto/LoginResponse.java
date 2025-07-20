package com.thmanyah.services.cms.model.dto;

import com.thmanyah.services.cms.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Login response with JWT tokens")
public class LoginResponse {

  @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;

  @Schema(description = "Token type", example = "Bearer")
  private String tokenType = "Bearer";

  @Schema(description = "Token expiration time in milliseconds", example = "86400000")
  private long expiresIn;

  @Schema(description = "User information")
  private UserInfo user;

  @Data
  @Builder
  @Schema(description = "User information")
  public static class UserInfo {
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "User email", example = "admin@thmanyah.io")
    private String email;

    @Schema(description = "User role", example = "ADMIN")
    private Role role;

    @Schema(description = "Whether user is active", example = "true")
    private Boolean isActive;
  }
}
