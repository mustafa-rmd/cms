package com.thmanyah.services.cms.controllers.impl;

import com.thmanyah.services.cms.controllers.ImportControllerV1;
import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.AuthorizationService;
import com.thmanyah.services.cms.services.ImportService;
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

@RestController
@RequestMapping("/api/v1/import")
@Slf4j
@RequiredArgsConstructor
public class ImportController implements ImportControllerV1 {

  private final ImportService importService;
  private final AuthorizationService authorizationService;
  private final Map<String, ExternalProviderPort> providers;

  @Override
  @PostMapping("/{provider}")
  public ResponseEntity<ImportJob> importFromProvider(
      @PathVariable String provider, @Valid @RequestBody ImportRequest request) {

    authorizationService.requireImportOperations("import from " + provider);
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info(
        "Import request from provider '{}' by user '{}' from {} to {}",
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
      if (!importService.isProviderAvailable(provider)) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
      }

      ImportJob job = importService.startImport(provider, request, userEmail);
      return ResponseEntity.ok(job);

    } catch (ExternalProviderException e) {
      log.error("Provider error during import: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      log.error("Unexpected error during import", e);
      return ResponseEntity.internalServerError().body(null);
    }
  }

  @Override
  @GetMapping("/jobs/{jobId}")
  public ResponseEntity<ImportJob> getImportJobStatus(@PathVariable UUID jobId) {
    log.debug("Getting status for import job: {}", jobId);

    try {
      ImportJob job = importService.getImportJob(jobId);
      return ResponseEntity.ok(job);
    } catch (Exception e) {
      log.error("Error getting import job status: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  @DeleteMapping("/jobs/{jobId}")
  public ResponseEntity<ImportJob> cancelImportJob(
      @PathVariable UUID jobId, @RequestHeader("X-User-Email") String userEmail) {

    log.info("Cancelling import job {} by user '{}'", jobId, userEmail);

    try {
      ImportJob job = importService.cancelImport(jobId, userEmail);
      return ResponseEntity.ok(job);
    } catch (Exception e) {
      log.error("Error cancelling import job: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    }
  }

  @Override
  @GetMapping("/jobs")
  public ResponseEntity<List<ImportJob>> getAllImportJobs() {
    log.debug("Getting all import jobs");

    try {
      List<ImportJob> jobs = importService.getAllImportJobs();
      return ResponseEntity.ok(jobs);
    } catch (Exception e) {
      log.error("Error getting all import jobs: {}", e.getMessage());
      return ResponseEntity.internalServerError().body(List.of());
    }
  }

  @Override
  @GetMapping("/providers")
  public ResponseEntity<Map<String, Object>> getAvailableProviders() {
    log.debug("Getting available providers");

    try {
      List<String> availableProviders = importService.getAvailableProviders();

      Map<String, Object> response = new HashMap<>();
      response.put("providers", availableProviders);
      response.put("count", availableProviders.size());

      // Add provider details
      Map<String, Map<String, Object>> providerDetails = new HashMap<>();
      for (String providerName : availableProviders) {
        ExternalProviderPort provider = providers.get(providerName);
        if (provider != null) {
          Map<String, Object> details = new HashMap<>();
          details.put("name", provider.getProviderName());
          details.put("available", provider.isAvailable());
          details.put("maxBatchSize", provider.getMaxBatchSize());
          details.put("rateLimitPerMinute", provider.getRateLimitPerMinute());
          providerDetails.put(providerName, details);
        }
      }
      response.put("details", providerDetails);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error getting available providers: {}", e.getMessage());
      return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get providers"));
    }
  }

  @Override
  @GetMapping("/providers/{provider}/health")
  public ResponseEntity<Map<String, Object>> checkProviderHealth(@PathVariable String provider) {
    log.debug("Checking health for provider: {}", provider);

    try {
      ExternalProviderPort providerPort = providers.get(provider);
      if (providerPort == null) {
        return ResponseEntity.notFound().build();
      }

      Map<String, Object> health = new HashMap<>();
      health.put("provider", provider);
      health.put("available", providerPort.isAvailable());
      health.put("maxBatchSize", providerPort.getMaxBatchSize());
      health.put("rateLimitPerMinute", providerPort.getRateLimitPerMinute());
      health.put("timestamp", java.time.LocalDateTime.now());

      return ResponseEntity.ok(health);
    } catch (Exception e) {
      log.error("Error checking provider health: {}", e.getMessage());
      return ResponseEntity.internalServerError().body(Map.of("error", "Health check failed"));
    }
  }
}
