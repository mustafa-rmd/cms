package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import java.util.List;
import java.util.UUID;

public interface ImportService {

  /**
   * Start an import job from an external provider
   *
   * @param provider Provider name (e.g., "youtube")
   * @param request Import request with date range and settings
   * @param userEmail Email of the user initiating the import
   * @return Import job with job ID
   */
  ImportJob startImport(String provider, ImportRequest request, String userEmail);

  /**
   * Get import job status and details
   *
   * @param jobId Job ID
   * @return Import job details
   */
  ImportJob getImportJob(UUID jobId);

  /**
   * Cancel an import job (if still in progress)
   *
   * @param jobId Job ID
   * @param userEmail Email of the user cancelling the job
   * @return Updated import job
   */
  ImportJob cancelImport(UUID jobId, String userEmail);

  /**
   * Get all import jobs (for monitoring/admin purposes)
   *
   * @return List of all import jobs
   */
  List<ImportJob> getAllImportJobs();

  /**
   * Get available providers
   *
   * @return List of available provider names
   */
  List<String> getAvailableProviders();

  /**
   * Check if a provider is available
   *
   * @param provider Provider name
   * @return true if available, false otherwise
   */
  boolean isProviderAvailable(String provider);
}
