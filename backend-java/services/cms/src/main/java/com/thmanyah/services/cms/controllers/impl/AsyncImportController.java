package com.thmanyah.services.cms.controllers.impl;

import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.ImportJob.ImportJobStatus;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.AuthorizationService;
import com.thmanyah.services.cms.services.impl.AsyncImportServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Enhanced Import Controller with async background processing support */
@RestController
@RequestMapping("/api/v1/import/async")
@Slf4j
@RequiredArgsConstructor
public class AsyncImportController {

  private final AsyncImportServiceImpl asyncImportService;
  private final AuthorizationService authorizationService;
  private final Map<String, ExternalProviderPort> providers;

  @Operation(summary = "Start async import", description = "Start an asynchronous import job")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "202", description = "Import job started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Provider not found"),
        @ApiResponse(responseCode = "503", description = "Provider unavailable")
      })
  @PostMapping("/{provider}")
  public ResponseEntity<ImportJob> startAsyncImport(
      @PathVariable String provider, @Valid @RequestBody ImportRequest request) {

    authorizationService.requireImportOperations("async import from " + provider);
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info(
        "Async import request from provider '{}' by user '{}' from {} to {}",
        provider,
        userEmail,
        request.startDate(),
        request.endDate());

    try {
      // Validate provider exists
      if (!providers.containsKey(provider)) {
        throw new ExternalProviderException(provider, "Unknown provider");
      }

      // Check provider availability
      if (!asyncImportService.isProviderAvailable(provider)) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
      }

      ImportJob job = asyncImportService.startImport(provider, request, userEmail);

      // Return 202 Accepted for async processing
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);

    } catch (ExternalProviderException e) {
      log.error("Provider error during async import: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      log.error("Unexpected error during async import", e);
      return ResponseEntity.internalServerError().body(null);
    }
  }

  @Operation(
      summary = "Get import job status",
      description = "Get the status and progress of an import job")
  @GetMapping("/jobs/{jobId}")
  public ResponseEntity<ImportJob> getImportJobStatus(@PathVariable UUID jobId) {
    try {
      ImportJob job = asyncImportService.getImportJob(jobId);
      return ResponseEntity.ok(job);
    } catch (Exception e) {
      log.error("Error getting import job status: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Cancel import job", description = "Cancel a running import job")
  @PostMapping("/jobs/{jobId}/cancel")
  public ResponseEntity<ImportJob> cancelImportJob(@PathVariable UUID jobId) {
    authorizationService.requireImportOperations("cancel import job");
    String userEmail = authorizationService.getCurrentUserEmail();

    try {
      ImportJob job = asyncImportService.cancelImport(jobId, userEmail);
      return ResponseEntity.ok(job);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      log.error("Error cancelling import job: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(summary = "Retry failed import job", description = "Retry a failed import job")
  @PostMapping("/jobs/{jobId}/retry")
  public ResponseEntity<ImportJob> retryImportJob(@PathVariable UUID jobId) {
    authorizationService.requireImportOperations("retry import job");
    String userEmail = authorizationService.getCurrentUserEmail();

    try {
      ImportJob job = asyncImportService.retryImport(jobId, userEmail);
      return ResponseEntity.ok(job);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      log.error("Error retrying import job: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @Operation(
      summary = "Get user's import jobs",
      description = "Get all import jobs for the current user")
  @GetMapping("/jobs/my")
  public ResponseEntity<List<ImportJob>> getMyImportJobs() {
    String userEmail = authorizationService.getCurrentUserEmail();
    List<ImportJob> jobs = asyncImportService.getJobsByUser(userEmail);
    return ResponseEntity.ok(jobs);
  }

  @Operation(
      summary = "Get import jobs by status",
      description = "Get import jobs filtered by status (ADMIN only)")
  @GetMapping("/jobs")
  public ResponseEntity<List<ImportJob>> getImportJobsByStatus(
      @Parameter(description = "Job status filter") @RequestParam(required = false)
          ImportJobStatus status) {

    authorizationService.requireUserManagement("view all import jobs");

    List<ImportJob> jobs =
        status != null
            ? asyncImportService.getJobsByStatus(status)
            : asyncImportService.getAllImportJobs();

    return ResponseEntity.ok(jobs);
  }

  @Operation(
      summary = "Get import statistics",
      description = "Get import job statistics and metrics")
  @GetMapping("/stats")
  public ResponseEntity<Map<String, Object>> getImportStatistics() {
    authorizationService.requireUserManagement("view import statistics");

    List<ImportJob> allJobs = asyncImportService.getAllImportJobs();

    Map<String, Object> stats = new HashMap<>();
    stats.put("totalJobs", allJobs.size());
    stats.put("activeJobs", asyncImportService.getActiveJobsCount());
    stats.put(
        "completedJobs",
        allJobs.stream().filter(j -> j.getStatus() == ImportJobStatus.COMPLETED).count());
    stats.put(
        "failedJobs",
        allJobs.stream().filter(j -> j.getStatus() == ImportJobStatus.FAILED).count());
    stats.put(
        "queuedJobs",
        allJobs.stream().filter(j -> j.getStatus() == ImportJobStatus.QUEUED).count());

    // Group by provider
    Map<String, Long> byProvider = new HashMap<>();
    allJobs.forEach(job -> byProvider.merge(job.getProvider(), 1L, Long::sum));
    stats.put("jobsByProvider", byProvider);

    return ResponseEntity.ok(stats);
  }

  @Operation(
      summary = "Get available providers",
      description = "Get list of available external content providers")
  @GetMapping("/providers")
  public ResponseEntity<Map<String, Object>> getAvailableProviders() {
    Map<String, Object> result = new HashMap<>();

    for (Map.Entry<String, ExternalProviderPort> entry : providers.entrySet()) {
      String providerName = entry.getKey();
      ExternalProviderPort provider = entry.getValue();

      Map<String, Object> providerInfo = new HashMap<>();
      providerInfo.put("available", provider.isAvailable());
      providerInfo.put("maxBatchSize", provider.getMaxBatchSize());
      providerInfo.put("rateLimitPerMinute", provider.getRateLimitPerMinute());

      result.put(providerName, providerInfo);
    }

    return ResponseEntity.ok(result);
  }
}
