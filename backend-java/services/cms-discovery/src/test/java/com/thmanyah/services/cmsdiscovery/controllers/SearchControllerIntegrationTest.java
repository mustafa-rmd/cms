package com.thmanyah.services.cmsdiscovery.controllers;

import com.thmanyah.services.cmsdiscovery.BaseIntegrationTest;
import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Search endpoints
 */
@DisplayName("Search Controller Integration Tests")
public class SearchControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should return empty results when no shows exist")
    void shouldReturnEmptyResultsWhenNoShowsExist() throws InterruptedException {
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?q=test")
        .then()
                .statusCode(200)
                .body("results", hasSize(0))
                .body("totalElements", equalTo(0))
                .body("totalPages", equalTo(0));
    }

    @Test
    @DisplayName("Should search shows by query")
    void shouldSearchShowsByQuery() throws InterruptedException {
        // Create test shows
        createTestShowDocument("Technology Podcast", "Latest tech trends and innovations");
        createTestShowDocument("Science Documentary", "Exploring the wonders of science");
        createTestShowDocument("Tech News", "Daily technology news updates");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?q=technology")
        .then()
                .statusCode(200)
                .body("results", hasSize(greaterThan(0)))
                .body("totalElements", greaterThan(0))
                .body("results[0].title", containsStringIgnoringCase("tech"));
    }

    @Test
    @DisplayName("Should filter shows by type")
    void shouldFilterShowsByType() throws InterruptedException {
        // Create test shows with different types
        createTestShowDocument("Podcast Show", "A great podcast", "podcast", "en");
        createTestShowDocument("Documentary Show", "An amazing documentary", "documentary", "en");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?type=podcast")
        .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("results[0].type", equalTo("podcast"));
    }

    @Test
    @DisplayName("Should filter shows by language")
    void shouldFilterShowsByLanguage() throws InterruptedException {
        // Create test shows with different languages
        createTestShowDocument("English Show", "English content", "podcast", "en");
        createTestShowDocument("Spanish Show", "Contenido en español", "podcast", "es");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?language=en")
        .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("results[0].language", equalTo("en"));
    }

    @Test
    @DisplayName("Should support pagination")
    void shouldSupportPagination() throws InterruptedException {
        // Create multiple test shows
        for (int i = 1; i <= 5; i++) {
            createTestShowDocument("Show " + i, "Description " + i);
        }
        
        waitForElasticsearchIndexing();

        // Test first page
        publicRequest()
        .when()
                .get("/api/v1/search/shows?page=0&size=2")
        .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("totalElements", equalTo(5))
                .body("totalPages", equalTo(3))
                .body("currentPage", equalTo(0))
                .body("pageSize", equalTo(2));

        // Test second page
        publicRequest()
        .when()
                .get("/api/v1/search/shows?page=1&size=2")
        .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("currentPage", equalTo(1));
    }

    @Test
    @DisplayName("Should perform advanced search with POST request")
    void shouldPerformAdvancedSearchWithPostRequest() throws InterruptedException {
        // Create test shows
        createTestShowDocument("AI Technology", "Artificial Intelligence trends", "documentary", "en");
        createTestShowDocument("Machine Learning", "ML algorithms explained", "podcast", "en");
        
        waitForElasticsearchIndexing();

        SearchRequest searchRequest = SearchRequest.builder()
                .query("technology")
                .type("documentary")
                .language("en")
                .page(0)
                .size(10)
                .sortBy("relevance")
                .sortDirection("desc")
                .highlight(true)
                .fuzzy(true)
                .build();

        publicRequest()
                .body(searchRequest)
        .when()
                .post("/api/v1/search/shows")
        .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("results[0].type", equalTo("documentary"));
    }

    @Test
    @DisplayName("Should get show by ID")
    void shouldGetShowById() throws InterruptedException {
        // Create test show
        ShowDocument show = createTestShowDocument("Test Show", "Test Description");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows/{showId}", show.getShowId())
        .then()
                .statusCode(200)
                .body("title", equalTo("Test Show"))
                .body("description", equalTo("Test Description"))
                .body("showId", equalTo(show.getShowId().toString()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent show")
    void shouldReturn404ForNonExistentShow() {
        UUID nonExistentId = UUID.randomUUID();

        publicRequest()
        .when()
                .get("/api/v1/search/shows/{showId}", nonExistentId)
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should get recent shows")
    void shouldGetRecentShows() throws InterruptedException {
        // Create test shows with different dates
        ShowDocument oldShow = createTestShowDocument("Old Show", "Old content");
        oldShow.setPublishedAt(LocalDate.now().minusDays(30));
        showRepository.save(oldShow);
        
        createTestShowDocument("Recent Show", "Recent content");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows/recent?page=0&size=10")
        .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("totalElements", equalTo(2));
    }

    @Test
    @DisplayName("Should get shows by type endpoint")
    void shouldGetShowsByTypeEndpoint() throws InterruptedException {
        // Create test shows
        createTestShowDocument("Podcast 1", "First podcast", "podcast", "en");
        createTestShowDocument("Podcast 2", "Second podcast", "podcast", "en");
        createTestShowDocument("Documentary 1", "First documentary", "documentary", "en");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows/type/podcast?page=0&size=10")
        .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("results[0].type", equalTo("podcast"))
                .body("results[1].type", equalTo("podcast"));
    }

    @Test
    @DisplayName("Should get shows by language endpoint")
    void shouldGetShowsByLanguageEndpoint() throws InterruptedException {
        // Create test shows
        createTestShowDocument("English Show 1", "English content 1", "podcast", "en");
        createTestShowDocument("English Show 2", "English content 2", "documentary", "en");
        createTestShowDocument("Spanish Show", "Contenido español", "podcast", "es");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows/language/en?page=0&size=10")
        .then()
                .statusCode(200)
                .body("results", hasSize(2))
                .body("results[0].language", equalTo("en"))
                .body("results[1].language", equalTo("en"));
    }

    @Test
    @DisplayName("Should handle CORS headers")
    void shouldHandleCorsHeaders() {
        publicRequest()
        .when()
                .options("/api/v1/search/shows")
        .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", notNullValue())
                .header("Access-Control-Allow-Methods", notNullValue());
    }

    @Test
    @DisplayName("Should validate search parameters")
    void shouldValidateSearchParameters() {
        // Test invalid page number
        publicRequest()
        .when()
                .get("/api/v1/search/shows?page=-1")
        .then()
                .statusCode(400);

        // Test invalid page size
        publicRequest()
        .when()
                .get("/api/v1/search/shows?size=1000")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should support fuzzy search")
    void shouldSupportFuzzySearch() throws InterruptedException {
        // Create test show
        createTestShowDocument("Technology Podcast", "Latest tech trends");
        
        waitForElasticsearchIndexing();

        // Search with typo
        publicRequest()
        .when()
                .get("/api/v1/search/shows?q=technolgy&fuzzy=true")
        .then()
                .statusCode(200)
                .body("results", hasSize(greaterThan(0)));
    }

    @Test
    @DisplayName("Should support search highlighting")
    void shouldSupportSearchHighlighting() throws InterruptedException {
        // Create test show
        createTestShowDocument("Technology Podcast", "Latest technology trends and innovations");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?q=technology&highlight=true")
        .then()
                .statusCode(200)
                .body("results", hasSize(greaterThan(0)));
    }

    @Test
    @DisplayName("Should filter by multiple criteria")
    void shouldFilterByMultipleCriteria() throws InterruptedException {
        // Create test shows
        createTestShowDocument("Tech Podcast EN", "English tech content", "podcast", "en");
        createTestShowDocument("Tech Documentary EN", "English tech documentary", "documentary", "en");
        createTestShowDocument("Tech Podcast ES", "Spanish tech content", "podcast", "es");
        
        waitForElasticsearchIndexing();

        publicRequest()
        .when()
                .get("/api/v1/search/shows?q=tech&type=podcast&language=en")
        .then()
                .statusCode(200)
                .body("results", hasSize(1))
                .body("results[0].type", equalTo("podcast"))
                .body("results[0].language", equalTo("en"));
    }
}
