package com.thmanyah.services.cmsdiscovery.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error response")
public class ValidationErrorResponse {

  @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "Error type", example = "Validation Failed")
  private String error;

  @Schema(description = "Error message", example = "Request validation failed")
  private String message;

  @Schema(description = "Request path", example = "/api/v1/search")
  private String path;

  @Schema(description = "Field validation errors")
  private Map<String, String> validationErrors;
}
