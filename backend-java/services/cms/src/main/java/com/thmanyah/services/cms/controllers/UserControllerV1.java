package com.thmanyah.services.cms.controllers;

import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.dto.UserCreateDto;
import com.thmanyah.services.cms.model.dto.UserDto;
import com.thmanyah.services.cms.model.dto.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "User Management", description = "User management operations (ADMIN only)")
@SecurityRequirement(name = "Bearer Authentication")
public interface UserControllerV1 {

  @Operation(summary = "Create a new user", description = "Create a new user (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
        @ApiResponse(responseCode = "409", description = "User already exists")
      })
  ResponseEntity<UserDto> createUser(
      @Parameter(description = "User creation data") UserCreateDto userCreateDto);

  @Operation(summary = "Update a user", description = "Update an existing user (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
      })
  ResponseEntity<UserDto> updateUser(
      @Parameter(description = "User ID") UUID id,
      @Parameter(description = "User update data") UserUpdateDto userUpdateDto);

  @Operation(
      summary = "Get all users",
      description = "Retrieve all users with pagination (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved users",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
      })
  ResponseEntity<Page<UserDto>> getAllUsers(
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Get users by role",
      description = "Retrieve users filtered by role (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved users",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
      })
  ResponseEntity<Page<UserDto>> getUsersByRole(
      @Parameter(description = "User role (ADMIN, EDITOR)", example = "EDITOR") Role role,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Get active users",
      description = "Retrieve active users with pagination (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active users",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
      })
  ResponseEntity<Page<UserDto>> getActiveUsers(
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(summary = "Get user by ID", description = "Retrieve a user by ID (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  ResponseEntity<UserDto> getUserById(@Parameter(description = "User ID") UUID id);

  @Operation(summary = "Delete a user", description = "Deactivate a user (ADMIN only)")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") UUID id);
}
