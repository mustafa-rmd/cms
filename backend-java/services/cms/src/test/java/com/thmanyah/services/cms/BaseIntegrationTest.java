package com.thmanyah.services.cms;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thmanyah.services.cms.config.TestContainersConfiguration;
import com.thmanyah.services.cms.model.Role;
import com.thmanyah.services.cms.model.User;
import com.thmanyah.services.cms.model.dto.LoginRequest;
import com.thmanyah.services.cms.model.dto.LoginResponse;
import com.thmanyah.services.cms.repositories.ShowRepository;
import com.thmanyah.services.cms.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Base class for integration tests with TestContainers */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

  @LocalServerPort protected int port;

  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected UserRepository userRepository;

  @Autowired protected ShowRepository showRepository;

  @Autowired protected PasswordEncoder passwordEncoder;

  @Autowired protected PostgreSQLContainer<?> postgresContainer;

  @Autowired protected RabbitMQContainer rabbitMQContainer;

  protected String adminToken;
  protected String editorToken;
  protected User adminUser;
  protected User editorUser;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    // Clean up data before each test
    showRepository.deleteAll();

    // Create test users and get tokens
    setupTestUsers();
    adminToken = getAuthToken("test-admin@thmanyah.io", "test-admin-password");
    editorToken = getAuthToken("test-editor@thmanyah.io", "editor-password");
  }

  private void setupTestUsers() {
    // Admin user should already exist from AdminUserInitializer
    adminUser =
        userRepository
            .findByEmailIgnoreCase("test-admin@thmanyah.io")
            .orElseThrow(() -> new RuntimeException("Admin user not found"));

    // Create editor user if not exists
    editorUser =
        userRepository
            .findByEmailIgnoreCase("test-editor@thmanyah.io")
            .orElseGet(
                () -> {
                  User editor =
                      User.builder()
                          .email("test-editor@thmanyah.io")
                          .password(passwordEncoder.encode("editor-password"))
                          .role(Role.EDITOR)
                          .isActive(true)
                          .createdBy("system")
                          .updatedBy("system")
                          .createdDate(LocalDateTime.now())
                          .updatedDate(LocalDateTime.now())
                          .build();
                  return userRepository.save(editor);
                });
  }

  protected String getAuthToken(String email, String password) {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(email);
    loginRequest.setPassword(password);

    Response response =
        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/api/v1/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    LoginResponse loginResponse = response.as(LoginResponse.class);
    return loginResponse.getAccessToken();
  }

  protected String getAdminAuthHeader() {
    return "Bearer " + adminToken;
  }

  protected String getEditorAuthHeader() {
    return "Bearer " + editorToken;
  }

  /** Helper method to create authenticated request with admin token */
  protected io.restassured.specification.RequestSpecification authenticatedAdminRequest() {
    return given().header("Authorization", getAdminAuthHeader()).contentType(ContentType.JSON);
  }

  /** Helper method to create authenticated request with editor token */
  protected io.restassured.specification.RequestSpecification authenticatedEditorRequest() {
    return given().header("Authorization", getEditorAuthHeader()).contentType(ContentType.JSON);
  }

  /** Helper method to create unauthenticated request */
  protected io.restassured.specification.RequestSpecification unauthenticatedRequest() {
    return given().contentType(ContentType.JSON);
  }

  /** Wait for RabbitMQ message processing */
  protected void waitForMessageProcessing() throws InterruptedException {
    Thread.sleep(1000); // Wait 1 second for async processing
  }

  /** Wait for async job processing */
  protected void waitForAsyncJobProcessing() throws InterruptedException {
    Thread.sleep(3000); // Wait 3 seconds for async job processing
  }
}
