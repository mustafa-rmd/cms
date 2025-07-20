package com.thmanyah.services.cms.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.thmanyah.services.cms.BaseIntegrationTest;
import com.thmanyah.services.cms.model.dto.LoginRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Integration tests for Authentication endpoints */
@DisplayName("Authentication Integration Tests")
public class AuthControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should login successfully with valid admin credentials")
  void shouldLoginSuccessfullyWithValidAdminCredentials() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test-admin@thmanyah.io");
    loginRequest.setPassword("test-admin-password");

    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("refreshToken", notNullValue())
        .body("tokenType", equalTo("Bearer"))
        .body("expiresIn", greaterThan(0))
        .body("user.email", equalTo("test-admin@thmanyah.io"))
        .body("user.role", equalTo("ADMIN"));
  }

  @Test
  @DisplayName("Should login successfully with valid editor credentials")
  void shouldLoginSuccessfullyWithValidEditorCredentials() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test-editor@thmanyah.io");
    loginRequest.setPassword("editor-password");

    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("refreshToken", notNullValue())
        .body("tokenType", equalTo("Bearer"))
        .body("user.email", equalTo("test-editor@thmanyah.io"))
        .body("user.role", equalTo("EDITOR"));
  }

  @Test
  @DisplayName("Should fail login with invalid credentials")
  void shouldFailLoginWithInvalidCredentials() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("invalid@email.com");
    loginRequest.setPassword("wrong-password");

    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("Should fail login with missing email")
  void shouldFailLoginWithMissingEmail() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("");
    loginRequest.setPassword("password");

    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName("Should fail login with missing password")
  void shouldFailLoginWithMissingPassword() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@email.com");
    loginRequest.setPassword("");

    given()
        .contentType(ContentType.JSON)
        .body(loginRequest)
        .when()
        .post("/api/v1/auth/login")
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName("Should refresh token successfully")
  void shouldRefreshTokenSuccessfully() {
    // First login to get refresh token
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test-admin@thmanyah.io");
    loginRequest.setPassword("test-admin-password");

    String refreshToken =
        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("refreshToken");

    // Use refresh token to get new access token
    given()
        .header("Authorization", "Bearer " + refreshToken)
        .when()
        .post("/api/v1/auth/refresh")
        .then()
        .statusCode(200)
        .body("accessToken", notNullValue())
        .body("refreshToken", notNullValue())
        .body("tokenType", equalTo("Bearer"));
  }

  @Test
  @DisplayName("Should logout successfully")
  void shouldLogoutSuccessfully() {
    authenticatedAdminRequest().when().post("/api/v1/auth/logout").then().statusCode(200);
  }

  @Test
  @DisplayName("Should fail logout without authentication")
  void shouldFailLogoutWithoutAuthentication() {
    unauthenticatedRequest().when().post("/api/v1/auth/logout").then().statusCode(401);
  }

  @Test
  @DisplayName("Should get current user info when authenticated")
  void shouldGetCurrentUserInfoWhenAuthenticated() {
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/auth/me")
        .then()
        .statusCode(200)
        .body("email", equalTo("test-admin@thmanyah.io"))
        .body("role", equalTo("ADMIN"))
        .body("isActive", equalTo(true));
  }

  @Test
  @DisplayName("Should fail to get user info without authentication")
  void shouldFailToGetUserInfoWithoutAuthentication() {
    unauthenticatedRequest().when().get("/api/v1/auth/me").then().statusCode(401);
  }
}
