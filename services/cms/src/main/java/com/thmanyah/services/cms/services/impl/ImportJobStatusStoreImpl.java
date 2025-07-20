package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.services.ImportJobStatusStore;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImportJobStatusStoreImpl implements ImportJobStatusStore {

  private final Map<UUID, ImportJob> jobStore = new ConcurrentHashMap<>();

  /** Store or update an import job */
  @Override
  public void storeJob(ImportJob job) {
    log.debug("Storing import job: {}", job.getJobId());
    jobStore.put(job.getJobId(), job);
  }

  /** Get an import job by ID */
  @Override
  public Optional<ImportJob> getJob(UUID jobId) {
    return Optional.ofNullable(jobStore.get(jobId));
  }

  /** Update job status */
  @Override
  public void updateJobStatus(UUID jobId, ImportJob.ImportJobStatus status) {
    ImportJob job = jobStore.get(jobId);
    if (job != null) {
      job.setStatus(status);
      job.setUpdatedAt(java.time.LocalDateTime.now());
      log.debug("Updated job {} status to {}", jobId, status);
    } else {
      log.warn("Attempted to update status for non-existent job: {}", jobId);
    }
  }

  /** Update job progress */
  @Override
  public void updateJobProgress(
      UUID jobId, int totalItems, int processedItems, int successfulItems, int failedItems) {
    ImportJob job = jobStore.get(jobId);
    if (job != null) {
      job.updateProgress(totalItems, processedItems, successfulItems, failedItems);
      log.debug(
          "Updated job {} progress: {}/{} items processed", jobId, processedItems, totalItems);
    } else {
      log.warn("Attempted to update progress for non-existent job: {}", jobId);
    }
  }

  /** Mark job as failed with error details */
  @Override
  public void markJobAsFailed(UUID jobId, String errorMessage, String errorDetails) {
    ImportJob job = jobStore.get(jobId);
    if (job != null) {
      job.markAsFailed(errorMessage, errorDetails);
      log.error("Marked job {} as failed: {}", jobId, errorMessage);
    } else {
      log.warn("Attempted to mark non-existent job as failed: {}", jobId);
    }
  }

  /** Mark job as completed */
  @Override
  public void markJobAsCompleted(UUID jobId) {
    ImportJob job = jobStore.get(jobId);
    if (job != null) {
      job.markAsCompleted();
      log.info("Marked job {} as completed", jobId);
    } else {
      log.warn("Attempted to mark non-existent job as completed: {}", jobId);
    }
  }

  /** Remove old completed jobs (cleanup) */
  @Override
  public void cleanupOldJobs() {
    java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusHours(24);

    jobStore
        .entrySet()
        .removeIf(
            entry -> {
              ImportJob job = entry.getValue();
              return job.isCompleted() && job.getUpdatedAt().isBefore(cutoff);
            });

    log.debug("Cleaned up old import jobs");
  }

  /** Get all jobs (for monitoring/admin purposes) */
  @Override
  public Map<UUID, ImportJob> getAllJobs() {
    return Map.copyOf(jobStore);
  }

  /** Check if job exists */
  @Override
  public boolean jobExists(UUID jobId) {
    return jobStore.containsKey(jobId);
  }

  /** Get job count */
  @Override
  public int getJobCount() {
    return jobStore.size();
  }
}
