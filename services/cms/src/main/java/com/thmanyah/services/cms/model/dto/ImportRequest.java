package com.thmanyah.services.cms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request for importing shows from external providers")
public record ImportRequest(
    @NotNull(message = "Topic is required")
        @Schema(description = "Topic for import", example = "education")
        String topic,
    @NotNull(message = "Start date is required")
        @Schema(description = "Start date for import (inclusive)", example = "2024-01-01")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
    @NotNull(message = "End date is required")
        @Schema(description = "End date for import (inclusive)", example = "2024-01-31")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
    @Schema(description = "Whether to skip duplicate titles", example = "true")
        Boolean skipDuplicates,
    @Schema(description = "Batch size for processing", example = "10") Integer batchSize) {

  public ImportRequest {
    // Validation in constructor
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before or equal to end date");
    }

    // Set defaults
    if (skipDuplicates == null) {
      skipDuplicates = true;
    }

    if (batchSize == null || batchSize <= 0) {
      batchSize = 10;
    }

    if (batchSize > 100) {
      batchSize = 100; // Maximum batch size
    }
  }

  /** Create a simple import request with just dates */
  public static ImportRequest of(String topic, LocalDate startDate, LocalDate endDate) {
    return new ImportRequest(topic, startDate, endDate, true, 10);
  }

  /** Create an import request with custom settings */
  public static ImportRequest withSettings(
      String topic, LocalDate startDate, LocalDate endDate, boolean skipDuplicates, int batchSize) {
    return new ImportRequest(topic, startDate, endDate, skipDuplicates, batchSize);
  }
}
