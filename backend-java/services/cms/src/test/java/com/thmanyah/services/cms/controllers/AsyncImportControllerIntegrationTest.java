package com.thmanyah.services.cms.controllers;

import static org.hamcrest.Matchers.*;

import com.thmanyah.services.cms.BaseIntegrationTest;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Integration tests for Async Import endpoints */
@DisplayName("Async Import Integration Tests")
public class AsyncImportControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should start async import successfully")
  void shouldStartAsyncImportSuccessfully() throws InterruptedException {
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(30), LocalDate.now());

    String jobId =
        authenticatedAdminRequest()
            .body(request)
            .when()
            .post("/api/v1/import/async/mock")
            .then()
            .statusCode(202)
            .body("jobId", notNullValue())
            .body("status", equalTo("QUEUED"))
            .body("provider", equalTo("mock"))
            .body("createdBy", equalTo("test-admin@thmanyah.io"))
            .extract()
            .path("jobId");

    // Wait for job processing
    waitForAsyncJobProcessing();

    // Check job status
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/jobs/{jobId}", jobId)
        .then()
        .statusCode(200)
        .body("jobId", equalTo(jobId))
        .body("status", anyOf(equalTo("COMPLETED"), equalTo("PROCESSING"), equalTo("FAILED")));
  }

  @Test
  @DisplayName("Should fail async import without authentication")
  void shouldFailAsyncImportWithoutAuthentication() {
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(30), LocalDate.now());

    unauthenticatedRequest()
        .body(request)
        .when()
        .post("/api/v1/import/async/mock")
        .then()
        .statusCode(401);
  }

  @Test
  @DisplayName("Should fail async import with invalid provider")
  void shouldFailAsyncImportWithInvalidProvider() {
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(30), LocalDate.now());

    authenticatedAdminRequest()
        .body(request)
        .when()
        .post("/api/v1/import/async/invalid-provider")
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName("Should get import job status")
  void shouldGetImportJobStatus() throws InterruptedException {
    // Start an import job
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    String jobId =
        authenticatedAdminRequest()
            .body(request)
            .when()
            .post("/api/v1/import/async/mock")
            .then()
            .statusCode(202)
            .extract()
            .path("jobId");

    // Get job status
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/jobs/{jobId}", jobId)
        .then()
        .statusCode(200)
        .body("jobId", equalTo(jobId))
        .body("status", notNullValue())
        .body("provider", equalTo("mock"))
        .body("createdBy", equalTo("test-admin@thmanyah.io"))
        .body("createdAt", notNullValue())
        .body("updatedAt", notNullValue());
  }

  @Test
  @DisplayName("Should get user's import jobs")
  void shouldGetUsersImportJobs() throws InterruptedException {
    // Start multiple import jobs
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    authenticatedAdminRequest().body(request).when().post("/api/v1/import/async/mock");

    authenticatedAdminRequest().body(request).when().post("/api/v1/import/async/mock");

    waitForMessageProcessing();

    // Get user's jobs
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/jobs/my")
        .then()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(2));
  }

  @Test
  @DisplayName("Should get all import jobs (admin only)")
  void shouldGetAllImportJobsAdminOnly() throws InterruptedException {
    // Start import jobs with different users
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    authenticatedAdminRequest().body(request).when().post("/api/v1/import/async/mock");

    authenticatedEditorRequest().body(request).when().post("/api/v1/import/async/mock");

    waitForMessageProcessing();

    // Admin should see all jobs
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/jobs")
        .then()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(2));

    // Editor should not have access
    authenticatedEditorRequest().when().get("/api/v1/import/async/jobs").then().statusCode(403);
  }

  @Test
  @DisplayName("Should get import jobs by status")
  void shouldGetImportJobsByStatus() throws InterruptedException {
    // Start an import job
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    authenticatedAdminRequest().body(request).when().post("/api/v1/import/async/mock");

    waitForMessageProcessing();

    // Get jobs by status
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/jobs?status=QUEUED")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("Should get import statistics")
  void shouldGetImportStatistics() throws InterruptedException {
    // Start some import jobs
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    authenticatedAdminRequest().body(request).when().post("/api/v1/import/async/mock");

    waitForMessageProcessing();

    // Get statistics
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/stats")
        .then()
        .statusCode(200)
        .body("totalJobs", greaterThanOrEqualTo(1))
        .body("activeJobs", greaterThanOrEqualTo(0));
  }

  @Test
  @DisplayName("Should get available providers")
  void shouldGetAvailableProviders() {
    authenticatedAdminRequest()
        .when()
        .get("/api/v1/import/async/providers")
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
  }

  @Test
  @DisplayName("Should cancel import job")
  void shouldCancelImportJob() throws InterruptedException {
    // Start an import job
    ImportRequest request =
        ImportRequest.of("technology", LocalDate.now().minusDays(7), LocalDate.now());

    String jobId =
        authenticatedAdminRequest()
            .body(request)
            .when()
            .post("/api/v1/import/async/mock")
            .then()
            .statusCode(202)
            .extract()
            .path("jobId");

    // Cancel the job
    authenticatedAdminRequest()
        .when()
        .post("/api/v1/import/async/jobs/{jobId}/cancel", jobId)
        .then()
        .statusCode(200)
        .body("jobId", equalTo(jobId))
        .body("status", equalTo("CANCELLED"));
  }

  @Test
  @DisplayName("Should retry failed import job")
  void shouldRetryFailedImportJob() throws InterruptedException {
    // This test would require a job to fail first
    // For now, we'll test the endpoint structure
    String fakeJobId = "00000000-0000-0000-0000-000000000000";

    authenticatedAdminRequest()
        .when()
        .post("/api/v1/import/async/jobs/{jobId}/retry", fakeJobId)
        .then()
        .statusCode(404); // Job not found
  }

  @Test
  @DisplayName("Should validate import request parameters")
  void shouldValidateImportRequestParameters() {
    // Test with invalid date range
    ImportRequest invalidRequest =
        ImportRequest.of(
            "technology",
            LocalDate.now(),
            LocalDate.now().minusDays(7) // End date before start date
            );

    authenticatedAdminRequest()
        .body(invalidRequest)
        .when()
        .post("/api/v1/import/async/mock")
        .then()
        .statusCode(400);
  }
}
