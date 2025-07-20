package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.UserCreateDto;
import com.thmanyah.services.cms.model.dto.UserDto;
import com.thmanyah.services.cms.model.dto.UserUpdateDto;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface UserService {

  /**
   * Create a new user
   *
   * @param creatorEmail Email of the user creating this user
   * @param userCreateDto User creation data
   * @return Created user DTO
   */
  UserDto createUser(String creatorEmail, UserCreateDto userCreateDto);

  /**
   * Update an existing user
   *
   * @param updaterEmail Email of the user updating this user
   * @param id User ID
   * @param userUpdateDto Updated user data
   * @return Updated user DTO
   */
  UserDto updateUser(String updaterEmail, UUID id, UserUpdateDto userUpdateDto);

  /**
   * Get all users with pagination
   *
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of user DTOs
   */
  Page<UserDto> getAllUsers(int pageNumber, int pageSize);

  /**
   * Get users by role with pagination
   *
   * @param role User role
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of user DTOs filtered by role
   */
  Page<UserDto> getUsersByRole(Role role, int pageNumber, int pageSize);

  /**
   * Get active users with pagination
   *
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of active user DTOs
   */
  Page<UserDto> getActiveUsers(int pageNumber, int pageSize);

  /**
   * Get user by ID as DTO
   *
   * @param id User ID
   * @return User DTO
   */
  UserDto getUserDtoById(UUID id);

  /**
   * Get user entity by ID
   *
   * @param id User ID
   * @return User entity
   */
  User getUserById(UUID id);

  /**
   * Get user by email
   *
   * @param email User email
   * @return User entity
   */
  User getUserByEmail(String email);

  /**
   * Delete a user (deactivate)
   *
   * @param deleterEmail Email of the user performing the deletion
   * @param id User ID
   */
  void deleteUser(String deleterEmail, UUID id);

  /**
   * Check if user exists by email
   *
   * @param email User email
   * @return true if exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Authenticate user with email and password
   *
   * @param email User email
   * @param password Plain text password
   * @return User entity if authentication successful, null otherwise
   */
  User authenticateUser(String email, String password);

  /**
   * Check if user has admin role
   *
   * @param email User email
   * @return true if user is admin, false otherwise
   */
  boolean isAdmin(String email);

  /**
   * Check if user has editor role or higher
   *
   * @param email User email
   * @return true if user is editor or admin, false otherwise
   */
  boolean isEditorOrAdmin(String email);
}
