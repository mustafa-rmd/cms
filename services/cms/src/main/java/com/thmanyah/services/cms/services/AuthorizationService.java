package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.Role;

/**
 * Service interface for authorization and access control operations. Handles role-based access
 * control and permission checking.
 */
public interface AuthorizationService {

  /**
   * Get current user email from request context
   *
   * @return Current user's email
   */
  String getCurrentUserEmail();

  /**
   * Check if current user has admin role, throw exception if not
   *
   * @param operation Operation being performed
   * @throws com.thmanyah.services.cms.exception.UnauthorizedException if user is not admin
   */
  void requireAdmin(String operation);

  /**
   * Check if current user has editor role or higher, throw exception if not
   *
   * @param operation Operation being performed
   * @throws com.thmanyah.services.cms.exception.UnauthorizedException if user is not editor or
   *     admin
   */
  void requireEditorOrAdmin(String operation);

  /**
   * Check if user has specific role
   *
   * @param userEmail User email
   * @param requiredRole Required role
   * @return true if user has the required role, false otherwise
   */
  boolean hasRole(String userEmail, Role requiredRole);

  /**
   * Check if current user can perform show management operations (create, read, update)
   *
   * @param operation Operation being performed
   */
  void requireShowManagement(String operation);

  /**
   * Check if current user can perform show deletion operations
   *
   * @param operation Operation being performed
   */
  void requireShowDeletion(String operation);

  /**
   * Check if current user can perform import operations
   *
   * @param operation Operation being performed
   */
  void requireImportOperations(String operation);

  /**
   * Check if current user can perform user management operations
   *
   * @param operation Operation being performed
   */
  void requireUserManagement(String operation);

  // Legacy methods for backward compatibility
  /**
   * @deprecated Use requireAdmin(String operation) instead
   */
  @Deprecated
  void requireAdmin(String userEmail, String operation);

  /**
   * @deprecated Use requireEditorOrAdmin(String operation) instead
   */
  @Deprecated
  void requireEditorOrAdmin(String userEmail, String operation);

  /**
   * @deprecated Use requireShowManagement(String operation) instead
   */
  @Deprecated
  void requireShowManagement(String userEmail, String operation);

  /**
   * @deprecated Use requireShowDeletion(String operation) instead
   */
  @Deprecated
  void requireShowDeletion(String userEmail, String operation);

  /**
   * @deprecated Use requireImportOperations(String operation) instead
   */
  @Deprecated
  void requireImportOperations(String userEmail, String operation);

  /**
   * @deprecated Use requireUserManagement(String operation) instead
   */
  @Deprecated
  void requireUserManagement(String userEmail, String operation);
}
