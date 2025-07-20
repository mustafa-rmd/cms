package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

  @Value("${cms.jwt.secret}")
  private String jwtSecret;

  @Value("${cms.jwt.expiration}")
  private long jwtExpiration;

  @Value("${cms.jwt.refresh-expiration}")
  private long refreshExpiration;

  /** Extract username (email) from JWT token */
  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /** Extract expiration date from JWT token */
  @Override
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /** Extract role from JWT token */
  @Override
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  /** Extract user ID from JWT token */
  @Override
  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", String.class));
  }

  /** Extract specific claim from JWT token */
  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /** Generate JWT token for user */
  @Override
  public String generateToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    claims.put("userId", user.getId().toString());
    claims.put("isActive", user.getIsActive());
    return createToken(claims, user.getEmail());
  }

  /** Generate refresh token for user */
  @Override
  public String generateRefreshToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "refresh");
    claims.put("userId", user.getId().toString());
    return createRefreshToken(claims, user.getEmail());
  }

  /** Validate JWT token */
  @Override
  public Boolean validateToken(String token, User user) {
    final String username = extractUsername(token);
    return (username.equals(user.getEmail()) && !isTokenExpired(token) && user.getIsActive());
  }

  /** Check if token is expired */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /** Extract all claims from JWT token */
  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
  }

  /** Create JWT token with claims and subject */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /** Create refresh token with claims and subject */
  private String createRefreshToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /** Get signing key for JWT */
  private Key getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /** Check if token is valid (not expired and properly formatted) */
  @Override
  public boolean isTokenValid(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      log.debug("Invalid token: {}", e.getMessage());
      return false;
    }
  }
}
