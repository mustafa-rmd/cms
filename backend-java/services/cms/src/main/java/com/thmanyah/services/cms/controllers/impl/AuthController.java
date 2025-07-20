package com.thmanyah.services.cms.controllers.impl;

import com.thmanyah.services.cms.controllers.AuthControllerV1;
import com.thmanyah.services.cms.model.dto.LoginRequest;
import com.thmanyah.services.cms.model.dto.LoginResponse;
import com.thmanyah.services.cms.model.dto.RefreshTokenRequest;
import com.thmanyah.services.cms.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerV1 {

  private final AuthenticationService authenticationService;

  @Override
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Login request for user: {}", loginRequest.getEmail());
    LoginResponse response = authenticationService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    log.info("Token refresh request");
    LoginResponse response = authenticationService.refreshToken(refreshTokenRequest);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // With JWT, logout is handled client-side by discarding the token
    // In a more sophisticated implementation, you might maintain a blacklist of tokens
    log.info("Logout request");
    return ResponseEntity.ok().build();
  }
}
