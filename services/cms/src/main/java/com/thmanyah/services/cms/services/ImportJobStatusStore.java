package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.ImportJob;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing import job status and storage. Provides in-memory storage and
 * management of import job states.
 */
public interface ImportJobStatusStore {

  /**
   * Store or update an import job
   *
   * @param job Import job to store
   */
  void storeJob(ImportJob job);

  /**
   * Get an import job by ID
   *
   * @param jobId Job ID
   * @return Optional containing the job if found
   */
  Optional<ImportJob> getJob(UUID jobId);

  /**
   * Update job status
   *
   * @param jobId Job ID
   * @param status New status
   */
  void updateJobStatus(UUID jobId, ImportJob.ImportJobStatus status);

  /**
   * Update job progress
   *
   * @param jobId Job ID
   * @param totalItems Total number of items to process
   * @param processedItems Number of items processed so far
   * @param successfulItems Number of successfully processed items
   * @param failedItems Number of failed items
   */
  void updateJobProgress(
      UUID jobId, int totalItems, int processedItems, int successfulItems, int failedItems);

  /**
   * Mark job as failed with error details
   *
   * @param jobId Job ID
   * @param errorMessage Error message
   * @param errorDetails Detailed error information
   */
  void markJobAsFailed(UUID jobId, String errorMessage, String errorDetails);

  /**
   * Mark job as completed
   *
   * @param jobId Job ID
   */
  void markJobAsCompleted(UUID jobId);

  /** Remove old completed jobs (cleanup) */
  void cleanupOldJobs();

  /**
   * Get all jobs (for monitoring/admin purposes)
   *
   * @return Map of all jobs keyed by job ID
   */
  Map<UUID, ImportJob> getAllJobs();

  /**
   * Check if job exists
   *
   * @param jobId Job ID
   * @return true if job exists, false otherwise
   */
  boolean jobExists(UUID jobId);

  /**
   * Get job count
   *
   * @return Total number of jobs in store
   */
  int getJobCount();
}
