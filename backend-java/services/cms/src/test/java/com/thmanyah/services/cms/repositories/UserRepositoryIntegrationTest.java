package com.thmanyah.services.cms.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.thmanyah.services.cms.BaseIntegrationTest;
import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.UserDto;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/** Integration tests for UserRepository */
@DisplayName("User Repository Integration Tests")
public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should save and find user by ID")
  void shouldSaveAndFindUserById() {
    User user = createTestUser("test@example.com", Role.EDITOR);
    User savedUser = userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(savedUser.getId());

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    assertThat(foundUser.get().getRole()).isEqualTo(Role.EDITOR);
  }

  @Test
  @DisplayName("Should find user by email")
  void shouldFindUserByEmail() {
    User user = createTestUser("findme@example.com", Role.ADMIN);
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findByEmail("findme@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("findme@example.com");
  }

  @Test
  @DisplayName("Should find user by email ignore case")
  void shouldFindUserByEmailIgnoreCase() {
    User user = createTestUser("CaseSensitive@Example.com", Role.EDITOR);
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findByEmailIgnoreCase("casesensitive@example.com");

    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("CaseSensitive@Example.com");
  }

  @Test
  @DisplayName("Should check if user exists by email")
  void shouldCheckIfUserExistsByEmail() {
    User user = createTestUser("exists@example.com", Role.EDITOR);
    userRepository.save(user);

    boolean exists = userRepository.existsByEmail("exists@example.com");
    boolean notExists = userRepository.existsByEmail("notexists@example.com");

    assertThat(exists).isTrue();
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("Should check if user exists by email ignore case")
  void shouldCheckIfUserExistsByEmailIgnoreCase() {
    User user = createTestUser("ExistsCase@Example.com", Role.EDITOR);
    userRepository.save(user);

    boolean exists = userRepository.existsByEmailIgnoreCase("existscase@example.com");
    boolean notExists = userRepository.existsByEmailIgnoreCase("notexists@example.com");

    assertThat(exists).isTrue();
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("Should find all users as DTOs with pagination")
  void shouldFindAllUsersAsDtosWithPagination() {
    userRepository.save(createTestUser("user1@example.com", Role.ADMIN));
    userRepository.save(createTestUser("user2@example.com", Role.EDITOR));
    userRepository.save(createTestUser("user3@example.com", Role.EDITOR));

    Pageable pageable = PageRequest.of(0, 2);
    Page<UserDto> results = userRepository.findAllUsersAsDto(pageable);

    // Note: Admin user from setup is also included
    assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(2);
    assertThat(results.getTotalElements()).isGreaterThanOrEqualTo(3);
  }

  @Test
  @DisplayName("Should find users by role as DTOs")
  void shouldFindUsersByRoleAsDtos() {
    userRepository.save(createTestUser("admin1@example.com", Role.ADMIN));
    userRepository.save(createTestUser("admin2@example.com", Role.ADMIN));
    userRepository.save(createTestUser("editor1@example.com", Role.EDITOR));

    Pageable pageable = PageRequest.of(0, 10);
    Page<UserDto> adminResults = userRepository.findByRoleAsDto(Role.ADMIN, pageable);
    Page<UserDto> editorResults = userRepository.findByRoleAsDto(Role.EDITOR, pageable);

    // Note: Test admin user from setup is also included
    assertThat(adminResults.getContent()).hasSizeGreaterThanOrEqualTo(2);
    assertThat(editorResults.getContent()).hasSizeGreaterThanOrEqualTo(1);

    assertThat(adminResults.getContent()).extracting(UserDto::getRole).containsOnly(Role.ADMIN);

    assertThat(editorResults.getContent()).extracting(UserDto::getRole).containsOnly(Role.EDITOR);
  }

  @Test
  @DisplayName("Should find active users as DTOs")
  void shouldFindActiveUsersAsDtos() {
    userRepository.save(createTestUser("active1@example.com", Role.EDITOR, true));
    userRepository.save(createTestUser("active2@example.com", Role.EDITOR, true));
    userRepository.save(createTestUser("inactive@example.com", Role.EDITOR, false));

    Pageable pageable = PageRequest.of(0, 10);
    Page<UserDto> results = userRepository.findActiveUsersAsDto(pageable);

    // All returned users should be active
    assertThat(results.getContent()).extracting(UserDto::getIsActive).containsOnly(true);

    // Should not include the inactive user
    assertThat(results.getContent())
        .extracting(UserDto::getEmail)
        .doesNotContain("inactive@example.com");
  }

  @Test
  @DisplayName("Should find user by ID as DTO")
  void shouldFindUserByIdAsDto() {
    User user = createTestUser("dto@example.com", Role.EDITOR);
    User savedUser = userRepository.save(user);

    Optional<UserDto> userDto = userRepository.findByIdAsDto(savedUser.getId());

    assertThat(userDto).isPresent();
    assertThat(userDto.get().getEmail()).isEqualTo("dto@example.com");
    assertThat(userDto.get().getRole()).isEqualTo(Role.EDITOR);
    assertThat(userDto.get().getId()).isEqualTo(savedUser.getId());
  }

  @Test
  @DisplayName("Should handle user with null fields gracefully")
  void shouldHandleUserWithNullFieldsGracefully() {
    User user =
        User.builder()
            .email("minimal@example.com")
            .password(passwordEncoder.encode("password"))
            .role(Role.EDITOR)
            .isActive(true)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            // createdBy and updatedBy are null
            .build();

    User savedUser = userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(savedUser.getId());
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("minimal@example.com");
  }

  @Test
  @DisplayName("Should enforce email uniqueness")
  void shouldEnforceEmailUniqueness() {
    User user1 = createTestUser("unique@example.com", Role.EDITOR);
    userRepository.save(user1);

    User user2 = createTestUser("unique@example.com", Role.ADMIN);

    // This should throw an exception due to unique constraint
    try {
      userRepository.save(user2);
      userRepository.flush(); // Force the constraint check
      assertThat(false).as("Expected unique constraint violation").isTrue();
    } catch (Exception e) {
      // Expected behavior - unique constraint violation
      assertThat(e.getMessage()).containsIgnoringCase("unique");
    }
  }

  private User createTestUser(String email, Role role) {
    return createTestUser(email, role, true);
  }

  private User createTestUser(String email, Role role, boolean isActive) {
    return User.builder()
        .email(email)
        .password(passwordEncoder.encode("password123"))
        .role(role)
        .isActive(isActive)
        .createdBy("test-system")
        .updatedBy("test-system")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();
  }
}
