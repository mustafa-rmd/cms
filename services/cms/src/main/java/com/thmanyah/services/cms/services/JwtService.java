package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.function.Function;

/**
 * Service interface for JWT token operations. Handles token generation, validation, and claim
 * extraction.
 */
public interface JwtService {

  /**
   * Extract username (email) from JWT token
   *
   * @param token JWT token
   * @return Username (email)
   */
  String extractUsername(String token);

  /**
   * Extract expiration date from JWT token
   *
   * @param token JWT token
   * @return Expiration date
   */
  Date extractExpiration(String token);

  /**
   * Extract role from JWT token
   *
   * @param token JWT token
   * @return Role as string
   */
  String extractRole(String token);

  /**
   * Extract user ID from JWT token
   *
   * @param token JWT token
   * @return User ID as string
   */
  String extractUserId(String token);

  /**
   * Extract specific claim from JWT token
   *
   * @param token JWT token
   * @param claimsResolver Function to extract specific claim
   * @return Extracted claim value
   */
  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  /**
   * Generate JWT token for user
   *
   * @param user User entity
   * @return Generated JWT token
   */
  String generateToken(User user);

  /**
   * Generate refresh token for user
   *
   * @param user User entity
   * @return Generated refresh token
   */
  String generateRefreshToken(User user);

  /**
   * Validate JWT token against user
   *
   * @param token JWT token
   * @param user User entity
   * @return true if token is valid for the user, false otherwise
   */
  Boolean validateToken(String token, User user);

  /**
   * Check if token is valid (not expired and properly formatted)
   *
   * @param token JWT token
   * @return true if token is valid, false otherwise
   */
  boolean isTokenValid(String token);
}
