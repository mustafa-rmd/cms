package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.exception.ImportJobException;
import com.thmanyah.services.cms.exception.ShowNotFoundException;
import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.ImportJobStatusStore;
import com.thmanyah.services.cms.services.ImportService;
import com.thmanyah.services.cms.services.ShowService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

  private final Map<String, ExternalProviderPort> providers;
  private final ShowService showService;
  private final ImportJobStatusStore jobStatusStore;

  @Override
  @Transactional
  public ImportJob startImport(String provider, ImportRequest request, String userEmail) {
    log.info("Starting import from provider: {} for user: {}", provider, userEmail);

    // Validate provider
    ExternalProviderPort providerPort = providers.get(provider);
    if (providerPort == null) {
      throw new ExternalProviderException(provider, "Unknown provider");
    }

    if (!providerPort.isAvailable()) {
      throw new ExternalProviderException(provider, "Provider is currently unavailable");
    }

    // Fetch and store data directly - no job complexity
    List<ShowExternalDto> externalShows =
        providerPort.fetch(request.topic(), request.startDate(), request.endDate());
    log.info("Fetched {} shows from provider", externalShows.size());

    // Batch save all shows to database
    List<Show> savedShows =
        showService.saveAllFromImport(userEmail, externalShows, request.skipDuplicates());
    int successfulItems = savedShows.size();

    log.info("Import completed. Total: {}, Successful: {}", externalShows.size(), successfulItems);

    // Return simple completed job
    ImportJob job = ImportJob.create(provider, request.startDate(), request.endDate(), userEmail);
    job.updateProgress(
        externalShows.size(),
        externalShows.size(),
        successfulItems,
        externalShows.size() - successfulItems);
    job.markAsCompleted();

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

    if (job.isCompleted()) {
      throw new ImportJobException(jobId, "Cannot cancel completed job");
    }

    job.setStatus(ImportJob.ImportJobStatus.CANCELLED);
    job.setUpdatedAt(java.time.LocalDateTime.now());
    jobStatusStore.storeJob(job);

    log.info("Import job {} cancelled by user '{}'", jobId, userEmail);
    return job;
  }

  @Override
  public List<ImportJob> getAllImportJobs() {
    return jobStatusStore.getAllJobs().values().stream().toList();
  }

  @Override
  public List<String> getAvailableProviders() {
    return providers.keySet().stream()
        .filter(providerName -> providers.get(providerName).isAvailable())
        .toList();
  }

  @Override
  public boolean isProviderAvailable(String provider) {
    ExternalProviderPort providerPort = providers.get(provider);
    return providerPort != null && providerPort.isAvailable();
  }
}
