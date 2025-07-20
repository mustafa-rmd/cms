package com.thmanyah.services.cms.controllers;

import static org.hamcrest.Matchers.*;

import com.thmanyah.services.cms.BaseIntegrationTest;
import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Integration tests for Show management endpoints */
@DisplayName("Show Management Integration Tests")
public class ShowControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should create show successfully with admin credentials")
  void shouldCreateShowSuccessfullyWithAdminCredentials() {
    ShowCreateUpdateDto request =
        ShowCreateUpdateDto.builder()
            .type("podcast")
            .title("Test Show")
            .description("Test Description")
            .language("en")
            .durationSec(3600)
            .publishedAt(LocalDate.now())
            .build();

    authenticatedAdminRequest()
        .body(request)
        .when()
        .post("/api/v1/shows")
        .then()
        .statusCode(201)
        .body("title", equalTo("Test Show"))
        .body("description", equalTo("Test Description"))
        .body("type", equalTo("podcast"))
        .body("language", equalTo("en"))
        .body("durationSec", equalTo(3600))
        .body("id", notNullValue())
        .body("createdBy", equalTo("test-admin@thmanyah.io"));
  }

  @Test
  @DisplayName("Should create show successfully with editor credentials")
  void shouldCreateShowSuccessfullyWithEditorCredentials() {
    ShowCreateUpdateDto request =
        ShowCreateUpdateDto.builder()
            .type("documentary")
            .title("Editor Show")
            .description("Editor Description")
            .language("en")
            .durationSec(7200)
            .publishedAt(LocalDate.now())
            .build();

    authenticatedEditorRequest()
        .body(request)
        .when()
        .post("/api/v1/shows")
        .then()
        .statusCode(201)
        .body("title", equalTo("Editor Show"))
        .body("createdBy", equalTo("test-editor@thmanyah.io"));
  }

  @Test
  @DisplayName("Should fail to create show without authentication")
  void shouldFailToCreateShowWithoutAuthentication() {
    ShowCreateUpdateDto request =
        ShowCreateUpdateDto.builder()
            .type("podcast")
            .title("Unauthorized Show")
            .description("Should fail")
            .language("en")
            .durationSec(3600)
            .publishedAt(LocalDate.now())
            .build();

    unauthenticatedRequest().body(request).when().post("/api/v1/shows").then().statusCode(401);
  }

  @Test
  @DisplayName("Should get all shows with pagination")
  void shouldGetAllShowsWithPagination() {
    // Create test shows
    createTestShow("Show 1", "Description 1");
    createTestShow("Show 2", "Description 2");
    createTestShow("Show 3", "Description 3");

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows?page=0&size=2")
        .then()
        .statusCode(200)
        .body("content", hasSize(2))
        .body("totalElements", equalTo(3))
        .body("totalPages", equalTo(2))
        .body("size", equalTo(2))
        .body("number", equalTo(0));
  }

  @Test
  @DisplayName("Should get show by ID")
  void shouldGetShowById() {
    Show createdShow = createTestShow("Test Show", "Test Description");

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows/{id}", createdShow.getId())
        .then()
        .statusCode(200)
        .body("id", equalTo(createdShow.getId().toString()))
        .body("title", equalTo("Test Show"))
        .body("description", equalTo("Test Description"));
  }

  @Test
  @DisplayName("Should return 404 for non-existent show")
  void shouldReturn404ForNonExistentShow() {
    UUID nonExistentId = UUID.randomUUID();

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows/{id}", nonExistentId)
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("Should update show successfully")
  void shouldUpdateShowSuccessfully() {
    Show createdShow = createTestShow("Original Title", "Original Description");

    ShowCreateUpdateDto updateRequest =
        ShowCreateUpdateDto.builder()
            .type("documentary")
            .title("Updated Title")
            .description("Updated Description")
            .language("es")
            .durationSec(7200)
            .publishedAt(LocalDate.now())
            .build();

    authenticatedAdminRequest()
        .body(updateRequest)
        .when()
        .put("/api/v1/shows/{id}", createdShow.getId())
        .then()
        .statusCode(200)
        .body("title", equalTo("Updated Title"))
        .body("description", equalTo("Updated Description"))
        .body("type", equalTo("documentary"))
        .body("language", equalTo("es"))
        .body("durationSec", equalTo(7200));
  }

  @Test
  @DisplayName("Should delete show successfully")
  void shouldDeleteShowSuccessfully() {
    Show createdShow = createTestShow("To Delete", "Will be deleted");

    authenticatedAdminRequest()
        .when()
        .delete("/api/v1/shows/{id}", createdShow.getId())
        .then()
        .statusCode(204);

    // Verify show is deleted
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows/{id}", createdShow.getId())
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("Should search shows by title")
  void shouldSearchShowsByTitle() {
    createTestShow("Technology Podcast", "About tech");
    createTestShow("Science Documentary", "About science");
    createTestShow("Tech News", "Latest tech news");

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows/search?title=tech")
        .then()
        .statusCode(200)
        .body("content", hasSize(greaterThan(0)));
  }

  @Test
  @DisplayName("Should filter shows by type")
  void shouldFilterShowsByType() {
    createTestShow("Podcast Show", "Description", "podcast");
    createTestShow("Documentary Show", "Description", "documentary");

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows?type=podcast")
        .then()
        .statusCode(200)
        .body("content", hasSize(1))
        .body("content[0].type", equalTo("podcast"));
  }

  @Test
  @DisplayName("Should filter shows by language")
  void shouldFilterShowsByLanguage() {
    createTestShow("English Show", "Description", "podcast", "en");
    createTestShow("Spanish Show", "Description", "documentary", "es");

    authenticatedAdminRequest()
        .when()
        .get("/api/v1/shows?language=en")
        .then()
        .statusCode(200)
        .body("content", hasSize(1))
        .body("content[0].language", equalTo("en"));
  }

  private Show createTestShow(String title, String description) {
    return createTestShow(title, description, "podcast", "en");
  }

  private Show createTestShow(String title, String description, String type) {
    return createTestShow(title, description, type, "en");
  }

  private Show createTestShow(String title, String description, String type, String language) {
    ShowCreateUpdateDto request =
        ShowCreateUpdateDto.builder()
            .type(type)
            .title(title)
            .description(description)
            .language(language)
            .durationSec(3600)
            .publishedAt(LocalDate.now())
            .build();

    String response =
        authenticatedAdminRequest()
            .body(request)
            .when()
            .post("/api/v1/shows")
            .then()
            .statusCode(201)
            .extract()
            .asString();

    try {
      return objectMapper.readValue(response, Show.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse show response", e);
    }
  }
}
