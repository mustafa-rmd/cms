package com.thmanyah.services.cms.controllers.impl;

import com.thmanyah.services.cms.controllers.UserControllerV1;
import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.dto.UserCreateDto;
import com.thmanyah.services.cms.model.dto.UserDto;
import com.thmanyah.services.cms.model.dto.UserUpdateDto;
import com.thmanyah.services.cms.services.AuthorizationService;
import com.thmanyah.services.cms.services.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserControllerV1 {

  private final UserService userService;
  private final AuthorizationService authorizationService;

  @Override
  @PostMapping("/users")
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {

    authorizationService.requireUserManagement("create user");
    String creatorEmail = authorizationService.getCurrentUserEmail();

    log.info("Creating user with email: {} by creator: {}", userCreateDto.getEmail(), creatorEmail);
    UserDto createdUser = userService.createUser(creatorEmail, userCreateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @Override
  @PutMapping("/users/{id}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable UUID id, @Valid @RequestBody UserUpdateDto userUpdateDto) {

    authorizationService.requireUserManagement("update user");
    String updaterEmail = authorizationService.getCurrentUserEmail();

    log.info("Updating user with ID: {} by updater: {}", id, updaterEmail);
    UserDto updatedUser = userService.updateUser(updaterEmail, id, userUpdateDto);
    return ResponseEntity.ok(updatedUser);
  }

  @Override
  @GetMapping("/users")
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    // Note: Authorization will be handled by security filter
    log.info("Getting all users - page: {}, size: {}", pageNumber, pageSize);
    Page<UserDto> users = userService.getAllUsers(pageNumber, pageSize);
    return ResponseEntity.ok(users);
  }

  @Override
  @GetMapping("/users/role/{role}")
  public ResponseEntity<Page<UserDto>> getUsersByRole(
      @PathVariable Role role,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting users by role: {} - page: {}, size: {}", role, pageNumber, pageSize);
    Page<UserDto> users = userService.getUsersByRole(role, pageNumber, pageSize);
    return ResponseEntity.ok(users);
  }

  @Override
  @GetMapping("/users/active")
  public ResponseEntity<Page<UserDto>> getActiveUsers(
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting active users - page: {}, size: {}", pageNumber, pageSize);
    Page<UserDto> users = userService.getActiveUsers(pageNumber, pageSize);
    return ResponseEntity.ok(users);
  }

  @Override
  @GetMapping("/users/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
    log.info("Getting user by ID: {}", id);
    UserDto user = userService.getUserDtoById(id);
    return ResponseEntity.ok(user);
  }

  @Override
  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {

    authorizationService.requireUserManagement("delete user");
    String deleterEmail = authorizationService.getCurrentUserEmail();

    log.info("Deleting user with ID: {} by deleter: {}", id, deleterEmail);
    userService.deleteUser(deleterEmail, id);
    return ResponseEntity.noContent().build();
  }
}
