package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.UserAlreadyExistsException;
import com.thmanyah.services.cms.exception.UserNotFoundException;
import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.UserCreateDto;
import com.thmanyah.services.cms.model.dto.UserDto;
import com.thmanyah.services.cms.model.dto.UserUpdateDto;
import com.thmanyah.services.cms.repositories.UserRepository;
import com.thmanyah.services.cms.services.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto createUser(String creatorEmail, UserCreateDto userCreateDto) {
    log.info("Creating user with email: {} by creator: {}", userCreateDto.getEmail(), creatorEmail);

    // Check if user already exists
    if (userRepository.existsByEmailIgnoreCase(userCreateDto.getEmail())) {
      throw new UserAlreadyExistsException(userCreateDto.getEmail());
    }

    // Create user entity
    User user =
        User.builder()
            .email(userCreateDto.getEmail().toLowerCase())
            .password(passwordEncoder.encode(userCreateDto.getPassword()))
            .role(userCreateDto.getRole())
            .isActive(true)
            .createdBy(creatorEmail)
            .updatedBy(creatorEmail)
            .build();

    User savedUser = userRepository.save(user);
    log.info("Successfully created user with ID: {}", savedUser.getId());

    return userRepository
        .findByIdAsDto(savedUser.getId())
        .orElseThrow(() -> new UserNotFoundException(savedUser.getId()));
  }

  @Override
  @Transactional
  public UserDto updateUser(String updaterEmail, UUID id, UserUpdateDto userUpdateDto) {
    log.info("Updating user with ID: {} by updater: {}", id, updaterEmail);

    User existingUser = getUserById(id);

    // Check if email is being changed and if it already exists
    if (userUpdateDto.getEmail() != null
        && !existingUser.getEmail().equalsIgnoreCase(userUpdateDto.getEmail())
        && userRepository.existsByEmailIgnoreCase(userUpdateDto.getEmail())) {
      throw new UserAlreadyExistsException(userUpdateDto.getEmail());
    }

    // Update fields if provided
    if (userUpdateDto.getEmail() != null) {
      existingUser.setEmail(userUpdateDto.getEmail().toLowerCase());
    }
    if (userUpdateDto.getPassword() != null) {
      existingUser.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
    }
    if (userUpdateDto.getRole() != null) {
      existingUser.setRole(userUpdateDto.getRole());
    }
    if (userUpdateDto.getIsActive() != null) {
      existingUser.setIsActive(userUpdateDto.getIsActive());
    }

    existingUser.setUpdatedBy(updaterEmail);
    existingUser.setUpdatedDate(LocalDateTime.now());

    User savedUser = userRepository.save(existingUser);
    log.info("Successfully updated user with ID: {}", savedUser.getId());

    return userRepository
        .findByIdAsDto(savedUser.getId())
        .orElseThrow(() -> new UserNotFoundException(savedUser.getId()));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> getAllUsers(int pageNumber, int pageSize) {
    log.debug("Getting all users - page: {}, size: {}", pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return userRepository.findAllUsersAsDto(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> getUsersByRole(Role role, int pageNumber, int pageSize) {
    log.debug("Getting users by role '{}' - page: {}, size: {}", role, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return userRepository.findByRoleAsDto(role, pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> getActiveUsers(int pageNumber, int pageSize) {
    log.debug("Getting active users - page: {}, size: {}", pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return userRepository.findActiveUsersAsDto(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserDtoById(UUID id) {
    log.debug("Getting user DTO by ID: {}", id);
    return userRepository.findByIdAsDto(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserById(UUID id) {
    log.debug("Getting user entity by ID: {}", id);
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    log.debug("Getting user by email: {}", email);
    return userRepository
        .findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
  }

  @Override
  @Transactional
  public void deleteUser(String deleterEmail, UUID id) {
    log.info("Deactivating user with ID: {} by deleter: {}", id, deleterEmail);

    User user = getUserById(id);
    user.setIsActive(false);
    user.setUpdatedBy(deleterEmail);
    user.setUpdatedDate(LocalDateTime.now());

    userRepository.save(user);
    log.info("Successfully deactivated user with ID: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmailIgnoreCase(email);
  }

  @Override
  @Transactional(readOnly = true)
  public User authenticateUser(String email, String password) {
    log.debug("Authenticating user with email: {}", email);

    User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
    if (user != null
        && user.getIsActive()
        && passwordEncoder.matches(password, user.getPassword())) {
      log.debug("Authentication successful for user: {}", email);
      return user;
    }

    log.debug("Authentication failed for user: {}", email);
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isAdmin(String email) {
    try {
      User user = getUserByEmail(email);
      return user.getIsActive() && user.getRole() == Role.ADMIN;
    } catch (UserNotFoundException e) {
      return false;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isEditorOrAdmin(String email) {
    try {
      User user = getUserByEmail(email);
      return user.getIsActive() && (user.getRole() == Role.ADMIN || user.getRole() == Role.EDITOR);
    } catch (UserNotFoundException e) {
      return false;
    }
  }
}
