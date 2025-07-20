package com.thmanyah.services.cms.repositories;

import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.UserDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  // Common query fragment for fetching UserDto
  String USER_DTO_QUERY =
      """
          SELECT new com.thmanyah.services.cms.model.dto.UserDto(
              u.id,
              u.email,
              u.role,
              u.isActive,
              u.createdDate,
              u.updatedDate,
              u.createdBy,
              u.updatedBy)
          FROM User u
      """;

  /**
   * Find user by email
   *
   * @param email User email
   * @return Optional User entity
   */
  Optional<User> findByEmail(String email);

  /**
   * Find user by email (case insensitive)
   *
   * @param email User email
   * @return Optional User entity
   */
  Optional<User> findByEmailIgnoreCase(String email);

  /**
   * Check if user exists by email
   *
   * @param email User email
   * @return true if exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Check if user exists by email (case insensitive)
   *
   * @param email User email
   * @return true if exists, false otherwise
   */
  boolean existsByEmailIgnoreCase(String email);

  /**
   * Get all users as DTOs with pagination
   *
   * @param pageable Pagination information
   * @return Page of UserDto
   */
  @Query(USER_DTO_QUERY + "ORDER BY u.createdDate DESC")
  Page<UserDto> findAllUsersAsDto(Pageable pageable);

  /**
   * Get users by role as DTOs with pagination
   *
   * @param role User role
   * @param pageable Pagination information
   * @return Page of UserDto
   */
  @Query(USER_DTO_QUERY + "WHERE u.role = :role ORDER BY u.createdDate DESC")
  Page<UserDto> findByRoleAsDto(@Param("role") Role role, Pageable pageable);

  /**
   * Get active users as DTOs with pagination
   *
   * @param pageable Pagination information
   * @return Page of UserDto
   */
  @Query(USER_DTO_QUERY + "WHERE u.isActive = true ORDER BY u.createdDate DESC")
  Page<UserDto> findActiveUsersAsDto(Pageable pageable);

  /**
   * Get user by ID as DTO
   *
   * @param id User ID
   * @return Optional UserDto
   */
  @Query(USER_DTO_QUERY + "WHERE u.id = :id")
  Optional<UserDto> findByIdAsDto(@Param("id") UUID id);
}
