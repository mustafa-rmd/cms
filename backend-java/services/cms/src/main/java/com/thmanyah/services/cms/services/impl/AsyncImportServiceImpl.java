package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.exception.ShowNotFoundException;
import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.ImportJob.ImportJobStatus;
import com.thmanyah.services.cms.model.dto.ImportJobMessage;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.ImportService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Asynchronous implementation of ImportService using RabbitMQ for background processing */
@Service("asyncImportService")
@Slf4j
@RequiredArgsConstructor
public class AsyncImportServiceImpl implements ImportService {

  private final Map<String, ExternalProviderPort> providers;
  private final JobStatusStore jobStatusStore;

  @Qualifier("importJobRabbitTemplate")
  private final RabbitTemplate importJobRabbitTemplate;

  @Override
  public ImportJob startImport(String provider, ImportRequest request, String userEmail) {
    log.info("Starting async import from provider: {} for user: {}", provider, userEmail);

    // Validate provider exists and is available
    ExternalProviderPort providerPort = providers.get(provider);
    if (providerPort == null) {
      throw new ExternalProviderException(provider, "Unknown provider");
    }

    if (!providerPort.isAvailable()) {
      throw new ExternalProviderException(provider, "Provider is currently unavailable");
    }

    // Create job record
    ImportJob job = ImportJob.create(provider, request.startDate(), request.endDate(), userEmail);
    job.setStatus(ImportJobStatus.QUEUED);
    job.setStatusMessage("Import job queued for processing");

    // Save job to store
    jobStatusStore.saveJob(job);

    // Create and send message to queue
    ImportJobMessage message =
        ImportJobMessage.fromRequest(job.getId(), provider, request, userEmail);

    try {
      importJobRabbitTemplate.convertAndSend(message);
      log.info("Import job {} queued successfully for provider: {}", job.getId(), provider);
    } catch (Exception e) {
      log.error("Failed to queue import job {}: {}", job.getId(), e.getMessage(), e);

      // Update job status to failed
      job.setStatus(ImportJobStatus.FAILED);
      job.setStatusMessage("Failed to queue job: " + e.getMessage());
      job.setCompletedAt(LocalDateTime.now());
      jobStatusStore.updateJob(job);

      throw new ExternalProviderException(provider, "Failed to queue import job", e);
    }

    return job;
  }

  @Override
  public ImportJob getImportJob(UUID jobId) {
    return jobStatusStore
        .getJob(jobId)
        .orElseThrow(
            () -> new ShowNotFoundException("Import job with id: " + jobId + " doesn't exist"));
  }

  @Override
  public ImportJob cancelImport(UUID jobId, String userEmail) {
    ImportJob job = getImportJob(jobId);

    // Only allow cancellation of active jobs
    if (!job.getStatus().isActive()) {
      throw new IllegalStateException("Cannot cancel job in status: " + job.getStatus());
    }

    // Check if user has permission to cancel (job owner or admin)
    if (!job.getCreatedBy().equals(userEmail)) {
      // Additional authorization check could be added here
      log.warn(
          "User {} attempted to cancel job {} created by {}", userEmail, jobId, job.getCreatedBy());
    }

    job.setStatus(ImportJobStatus.CANCELLED);
    job.setStatusMessage("Job cancelled by user: " + userEmail);
    job.setCompletedAt(LocalDateTime.now());

    jobStatusStore.updateJob(job);

    log.info("Import job {} cancelled by user: {}", jobId, userEmail);
    return job;
  }

  @Override
  public List<ImportJob> getAllImportJobs() {
    return jobStatusStore.getAllJobs();
  }

  @Override
  public List<String> getAvailableProviders() {
    return providers.entrySet().stream()
        .filter(entry -> entry.getValue().isAvailable())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isProviderAvailable(String provider) {
    ExternalProviderPort providerPort = providers.get(provider);
    return providerPort != null && providerPort.isAvailable();
  }

  /** Get jobs by status - useful for monitoring */
  public List<ImportJob> getJobsByStatus(ImportJobStatus status) {
    return jobStatusStore.getAllJobs().stream()
        .filter(job -> job.getStatus() == status)
        .collect(Collectors.toList());
  }

  /** Get jobs by user - useful for user dashboard */
  public List<ImportJob> getJobsByUser(String userEmail) {
    return jobStatusStore.getAllJobs().stream()
        .filter(job -> job.getCreatedBy().equals(userEmail))
        .collect(Collectors.toList());
  }

  /** Get active jobs count - useful for load monitoring */
  public long getActiveJobsCount() {
    return jobStatusStore.getAllJobs().stream().filter(job -> job.getStatus().isActive()).count();
  }

  /** Retry a failed job */
  public ImportJob retryImport(UUID jobId, String userEmail) {
    ImportJob job = getImportJob(jobId);

    if (job.getStatus() != ImportJobStatus.FAILED) {
      throw new IllegalStateException(
          "Can only retry failed jobs. Current status: " + job.getStatus());
    }

    // Create new job message for retry
    ImportJobMessage message =
        ImportJobMessage.builder()
            .jobId(job.getId())
            .provider(job.getProvider())
            .userEmail(userEmail)
            .topic("") // Would need to store this in job
            .startDate(job.getStartDate())
            .endDate(job.getEndDate())
            .skipDuplicates(true)
            .retryCount(0)
            .maxRetries(3)
            .createdAt(LocalDateTime.now())
            .scheduledAt(LocalDateTime.now())
            .build();

    // Reset job status
    job.setStatus(ImportJobStatus.QUEUED);
    job.setStatusMessage("Job manually retried by user: " + userEmail);
    job.setCompletedAt(null);

    jobStatusStore.updateJob(job);

    // Send to queue
    importJobRabbitTemplate.convertAndSend(message);

    log.info("Import job {} manually retried by user: {}", jobId, userEmail);
    return job;
  }
}
