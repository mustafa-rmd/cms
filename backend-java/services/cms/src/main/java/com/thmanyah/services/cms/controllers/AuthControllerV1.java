package com.thmanyah.services.cms.controllers;

import com.thmanyah.services.cms.model.dto.LoginRequest;
import com.thmanyah.services.cms.model.dto.LoginResponse;
import com.thmanyah.services.cms.model.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication", description = "Authentication operations")
public interface AuthControllerV1 {

  @Operation(summary = "User login", description = "Authenticate user and get JWT tokens")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
      })
  ResponseEntity<LoginResponse> login(
      @Parameter(description = "Login credentials") LoginRequest loginRequest);

  @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
      })
  ResponseEntity<LoginResponse> refreshToken(
      @Parameter(description = "Refresh token request") RefreshTokenRequest refreshTokenRequest);

  @Operation(summary = "Logout", description = "Logout user (client should discard tokens)")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
      })
  ResponseEntity<Void> logout();
}
