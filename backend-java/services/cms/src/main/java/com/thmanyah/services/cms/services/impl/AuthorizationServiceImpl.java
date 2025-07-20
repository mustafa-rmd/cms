package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.UnauthorizedException;
import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.services.AuthorizationService;
import com.thmanyah.services.cms.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {

  private final UserService userService;

  /** Get current user from request context (set by JWT filter) */
  private User getCurrentUser() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      User user = (User) request.getAttribute("currentUser");
      if (user != null) {
        return user;
      }
    }
    throw new UnauthorizedException("No authenticated user found");
  }

  /** Get current user email from request context */
  @Override
  public String getCurrentUserEmail() {
    return getCurrentUser().getEmail();
  }

  /**
   * Check if current user has admin role, throw exception if not
   *
   * @param operation Operation being performed
   * @throws UnauthorizedException if user is not admin
   */
  @Override
  public void requireAdmin(String operation) {
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != Role.ADMIN) {
      log.warn(
          "Access denied for user '{}' attempting operation '{}' - requires ADMIN role",
          currentUser.getEmail(),
          operation);
      throw new UnauthorizedException(operation, "ADMIN");
    }
  }

  /**
   * Check if user has admin role, throw exception if not (legacy method for backward compatibility)
   *
   * @param userEmail User email (ignored, uses current user from JWT)
   * @param operation Operation being performed
   * @throws UnauthorizedException if user is not admin
   */
  @Override
  @Deprecated
  public void requireAdmin(String userEmail, String operation) {
    requireAdmin(operation);
  }

  /**
   * Check if current user has editor role or higher, throw exception if not
   *
   * @param operation Operation being performed
   * @throws UnauthorizedException if user is not editor or admin
   */
  @Override
  public void requireEditorOrAdmin(String operation) {
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EDITOR) {
      log.warn(
          "Access denied for user '{}' attempting operation '{}' - requires EDITOR or ADMIN role",
          currentUser.getEmail(),
          operation);
      throw new UnauthorizedException(operation, "EDITOR or ADMIN");
    }
  }

  /**
   * Check if user has editor role or higher, throw exception if not (legacy method for backward
   * compatibility)
   *
   * @param userEmail User email (ignored, uses current user from JWT)
   * @param operation Operation being performed
   * @throws UnauthorizedException if user is not editor or admin
   */
  @Override
  @Deprecated
  public void requireEditorOrAdmin(String userEmail, String operation) {
    requireEditorOrAdmin(operation);
  }

  /**
   * Check if user has specific role
   *
   * @param userEmail User email
   * @param requiredRole Required role
   * @return true if user has the required role, false otherwise
   */
  @Override
  public boolean hasRole(String userEmail, Role requiredRole) {
    try {
      var user = userService.getUserByEmail(userEmail);
      return user.getIsActive() && user.getRole() == requiredRole;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Check if current user can perform show management operations (create, read, update)
   *
   * @param operation Operation being performed
   */
  @Override
  public void requireShowManagement(String operation) {
    requireEditorOrAdmin(operation);
  }

  /**
   * Check if current user can perform show deletion operations
   *
   * @param operation Operation being performed
   */
  @Override
  public void requireShowDeletion(String operation) {
    requireAdmin(operation);
  }

  /**
   * Check if current user can perform import operations
   *
   * @param operation Operation being performed
   */
  @Override
  public void requireImportOperations(String operation) {
    requireAdmin(operation);
  }

  /**
   * Check if current user can perform user management operations
   *
   * @param operation Operation being performed
   */
  @Override
  public void requireUserManagement(String operation) {
    requireAdmin(operation);
  }

  // Legacy methods for backward compatibility
  @Override
  @Deprecated
  public void requireShowManagement(String userEmail, String operation) {
    requireShowManagement(operation);
  }

  @Override
  @Deprecated
  public void requireShowDeletion(String userEmail, String operation) {
    requireShowDeletion(operation);
  }

  @Override
  @Deprecated
  public void requireImportOperations(String userEmail, String operation) {
    requireImportOperations(operation);
  }

  @Override
  @Deprecated
  public void requireUserManagement(String userEmail, String operation) {
    requireUserManagement(operation);
  }
}
