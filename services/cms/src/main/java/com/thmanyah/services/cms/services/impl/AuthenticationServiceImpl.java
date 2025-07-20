package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.UnauthorizedException;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.LoginRequest;
import com.thmanyah.services.cms.model.dto.LoginResponse;
import com.thmanyah.services.cms.model.dto.RefreshTokenRequest;
import com.thmanyah.services.cms.services.AuthenticationService;
import com.thmanyah.services.cms.services.JwtService;
import com.thmanyah.services.cms.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final UserService userService;
  private final JwtService jwtService;

  @Value("${cms.jwt.expiration}")
  private long jwtExpiration;

  /** Authenticate user and generate JWT tokens */
  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    log.info("Login attempt for user: {}", loginRequest.getEmail());

    // Authenticate user
    User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    if (user == null) {
      log.warn("Authentication failed for user: {}", loginRequest.getEmail());
      throw new UnauthorizedException("Invalid email or password");
    }

    if (!user.getIsActive()) {
      log.warn("Login attempt for inactive user: {}", loginRequest.getEmail());
      throw new UnauthorizedException("User account is deactivated");
    }

    // Generate tokens
    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    log.info("Successful login for user: {} with role: {}", user.getEmail(), user.getRole());

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtExpiration)
        .user(
            LoginResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build())
        .build();
  }

  /** Refresh access token using refresh token */
  @Override
  public LoginResponse refreshToken(RefreshTokenRequest refreshRequest) {
    String refreshToken = refreshRequest.getRefreshToken();

    try {
      // Validate refresh token
      if (!jwtService.isTokenValid(refreshToken)) {
        throw new UnauthorizedException("Invalid or expired refresh token");
      }

      // Extract user from refresh token
      String userEmail = jwtService.extractUsername(refreshToken);
      User user = userService.getUserByEmail(userEmail);

      if (!user.getIsActive()) {
        throw new UnauthorizedException("User account is deactivated");
      }

      // Generate new tokens
      String newAccessToken = jwtService.generateToken(user);
      String newRefreshToken = jwtService.generateRefreshToken(user);

      log.info("Token refreshed for user: {}", user.getEmail());

      return LoginResponse.builder()
          .accessToken(newAccessToken)
          .refreshToken(newRefreshToken)
          .tokenType("Bearer")
          .expiresIn(jwtExpiration)
          .user(
              LoginResponse.UserInfo.builder()
                  .id(user.getId())
                  .email(user.getEmail())
                  .role(user.getRole())
                  .isActive(user.getIsActive())
                  .build())
          .build();

    } catch (Exception e) {
      log.warn("Token refresh failed: {}", e.getMessage());
      throw new UnauthorizedException("Invalid or expired refresh token");
    }
  }

  /** Get user from JWT token */
  @Override
  public User getUserFromToken(String token) {
    try {
      if (!jwtService.isTokenValid(token)) {
        throw new UnauthorizedException("Invalid or expired token");
      }

      String userEmail = jwtService.extractUsername(token);
      User user = userService.getUserByEmail(userEmail);

      if (!user.getIsActive()) {
        throw new UnauthorizedException("User account is deactivated");
      }

      // Validate token against user
      if (!jwtService.validateToken(token, user)) {
        throw new UnauthorizedException("Token validation failed");
      }

      return user;

    } catch (Exception e) {
      log.debug("Token validation failed: {}", e.getMessage());
      throw new UnauthorizedException("Invalid or expired token");
    }
  }
}
