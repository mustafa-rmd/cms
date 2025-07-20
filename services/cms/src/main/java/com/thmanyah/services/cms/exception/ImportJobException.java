package com.thmanyah.services.cms.exception;

import java.util.UUID;

public class ImportJobException extends RuntimeException {

  private final UUID jobId;

  public ImportJobException(UUID jobId, String message) {
    super(String.format("Import job '%s': %s", jobId, message));
    this.jobId = jobId;
  }

  public ImportJobException(UUID jobId, String message, Throwable cause) {
    super(String.format("Import job '%s': %s", jobId, message), cause);
    this.jobId = jobId;
  }

  public UUID getJobId() {
    return jobId;
  }
}
