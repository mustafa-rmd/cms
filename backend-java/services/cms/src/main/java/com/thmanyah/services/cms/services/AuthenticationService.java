package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.LoginRequest;
import com.thmanyah.services.cms.model.dto.LoginResponse;
import com.thmanyah.services.cms.model.dto.RefreshTokenRequest;

/**
 * Service interface for user authentication operations. Handles login, token refresh, and token
 * validation.
 */
public interface AuthenticationService {

  /**
   * Authenticate user and generate JWT tokens
   *
   * @param loginRequest Login credentials
   * @return Login response with tokens and user info
   */
  LoginResponse login(LoginRequest loginRequest);

  /**
   * Refresh access token using refresh token
   *
   * @param refreshRequest Refresh token request
   * @return New login response with refreshed tokens
   */
  LoginResponse refreshToken(RefreshTokenRequest refreshRequest);

  /**
   * Get user from JWT token
   *
   * @param token JWT token
   * @return User entity if token is valid
   */
  User getUserFromToken(String token);
}
