package com.thmanyah.services.cms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportJob {

  private UUID jobId;
  private String provider;
  private LocalDate startDate;
  private LocalDate endDate;
  private ImportJobStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private Integer totalItems;
  private Integer processedItems;
  private Integer successfulItems;
  private Integer failedItems;
  private String errorMessage;
  private String errorDetails;

  public enum ImportJobStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
  }

  public static ImportJob create(
      String provider, LocalDate startDate, LocalDate endDate, String createdBy) {
    LocalDateTime now = LocalDateTime.now();
    return ImportJob.builder()
        .jobId(UUID.randomUUID())
        .provider(provider)
        .startDate(startDate)
        .endDate(endDate)
        .status(ImportJobStatus.PENDING)
        .createdAt(now)
        .updatedAt(now)
        .createdBy(createdBy)
        .totalItems(0)
        .processedItems(0)
        .successfulItems(0)
        .failedItems(0)
        .build();
  }

  public void updateProgress(
      int totalItems, int processedItems, int successfulItems, int failedItems) {
    this.totalItems = totalItems;
    this.processedItems = processedItems;
    this.successfulItems = successfulItems;
    this.failedItems = failedItems;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsCompleted() {
    this.status = ImportJobStatus.COMPLETED;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsFailed(String errorMessage, String errorDetails) {
    this.status = ImportJobStatus.FAILED;
    this.errorMessage = errorMessage;
    this.errorDetails = errorDetails;
    this.updatedAt = LocalDateTime.now();
  }

  public void markAsInProgress() {
    this.status = ImportJobStatus.IN_PROGRESS;
    this.updatedAt = LocalDateTime.now();
  }

  public double getProgressPercentage() {
    if (totalItems == null || totalItems == 0) {
      return 0.0;
    }
    return (double) (processedItems != null ? processedItems : 0) / totalItems * 100.0;
  }

  public boolean isCompleted() {
    return status == ImportJobStatus.COMPLETED
        || status == ImportJobStatus.FAILED
        || status == ImportJobStatus.CANCELLED;
  }
}
