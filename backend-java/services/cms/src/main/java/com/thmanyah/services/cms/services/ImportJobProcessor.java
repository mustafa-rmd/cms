package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.ImportJob.ImportJobStatus;
import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ImportJobMessage;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.impl.JobStatusStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Background processor for import jobs using RabbitMQ */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportJobProcessor {

  private final Map<String, ExternalProviderPort> providers;
  private final ShowService showService;
  private final JobStatusStore jobStatusStore;

  @Qualifier("importJobRabbitTemplate")
  private final RabbitTemplate importJobRabbitTemplate;

  /** Process import job messages from the queue */
  @RabbitListener(
      queues = "${cms.import.rabbitmq.queue:cms.import.queue}",
      containerFactory = "importJobListenerContainerFactory")
  @Transactional
  public void processImportJob(ImportJobMessage message) {
    log.info(
        "Processing import job: {} for provider: {} by user: {}",
        message.getJobId(),
        message.getProvider(),
        message.getUserEmail());

    ImportJob job = getOrCreateJob(message);

    try {
      // Update status to processing
      updateJobStatus(job, ImportJobStatus.PROCESSING, "Starting import process");

      // Validate provider
      ExternalProviderPort provider = providers.get(message.getProvider());
      if (provider == null) {
        throw new ExternalProviderException(message.getProvider(), "Unknown provider");
      }

      if (!provider.isAvailable()) {
        throw new ExternalProviderException(
            message.getProvider(), "Provider is currently unavailable");
      }

      // Fetch data from external provider
      updateJobStatus(job, ImportJobStatus.FETCHING, "Fetching data from " + message.getProvider());

      List<ShowExternalDto> externalShows =
          provider.fetch(message.getTopic(), message.getStartDate(), message.getEndDate());

      log.info("Fetched {} shows from provider {}", externalShows.size(), message.getProvider());

      // Update progress
      job.updateProgress(externalShows.size(), 0, 0, 0);
      jobStatusStore.updateJob(job);

      // Save to database in batches
      updateJobStatus(job, ImportJobStatus.SAVING, "Saving data to database");

      List<Show> savedShows =
          showService.saveAllFromImport(
              message.getUserEmail(), externalShows, message.isSkipDuplicates());

      int successfulItems = savedShows.size();
      int failedItems = externalShows.size() - successfulItems;

      // Complete the job
      job.updateProgress(externalShows.size(), externalShows.size(), successfulItems, failedItems);
      job.markAsCompleted();
      job.setCompletedAt(LocalDateTime.now());

      updateJobStatus(
          job,
          ImportJobStatus.COMPLETED,
          String.format(
              "Import completed. Total: %d, Successful: %d, Failed: %d",
              externalShows.size(), successfulItems, failedItems));

      log.info(
          "Import job {} completed successfully. Total: {}, Successful: {}, Failed: {}",
          job.getId(),
          externalShows.size(),
          successfulItems,
          failedItems);

    } catch (Exception e) {
      log.error("Import job {} failed: {}", message.getJobId(), e.getMessage(), e);
      handleJobFailure(job, message, e);
    }
  }

  private ImportJob getOrCreateJob(ImportJobMessage message) {
    return jobStatusStore
        .getJob(message.getJobId())
        .orElseGet(
            () -> {
              ImportJob job =
                  ImportJob.create(
                      message.getProvider(),
                      message.getStartDate(),
                      message.getEndDate(),
                      message.getUserEmail());
              job.setId(message.getJobId());
              job.setStatus(ImportJobStatus.QUEUED);
              jobStatusStore.saveJob(job);
              return job;
            });
  }

  private void updateJobStatus(ImportJob job, ImportJobStatus status, String message) {
    job.setStatus(status);
    job.setStatusMessage(message);
    jobStatusStore.updateJob(job);

    log.info("Job {} status updated to: {} - {}", job.getId(), status, message);
  }

  private void handleJobFailure(ImportJob job, ImportJobMessage message, Exception error) {
    String errorMessage = "Import failed: " + error.getMessage();

    if (message.canRetry()) {
      // Schedule retry
      updateJobStatus(
          job,
          ImportJobStatus.RETRYING,
          String.format(
              "Retrying import (attempt %d/%d): %s",
              message.getRetryCount() + 1, message.getMaxRetries(), error.getMessage()));

      ImportJobMessage retryMessage = message.withRetry();

      // Send to retry queue with delay
      importJobRabbitTemplate.convertAndSend("import.retry", retryMessage);

      log.warn(
          "Import job {} failed, scheduling retry {}/{}: {}",
          message.getJobId(),
          retryMessage.getRetryCount(),
          message.getMaxRetries(),
          error.getMessage());
    } else {
      // Mark as permanently failed
      job.setStatus(ImportJobStatus.FAILED);
      job.setStatusMessage(errorMessage);
      job.setCompletedAt(LocalDateTime.now());
      jobStatusStore.updateJob(job);

      log.error(
          "Import job {} permanently failed after {} retries: {}",
          message.getJobId(),
          message.getMaxRetries(),
          error.getMessage());
    }
  }
}
