package com.thmanyah.services.cms.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response structure")
public class ErrorResponse {

  @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code", example = "404")
  private int status;

  @Schema(description = "HTTP status reason phrase", example = "Not Found")
  private String error;

  @Schema(
      description = "Error message",
      example = "Show with id: 123e4567-e89b-12d3-a456-426614174000 doesn't exist")
  private String message;

  @Schema(
      description = "Request path that caused the error",
      example = "/api/v1/shows/123e4567-e89b-12d3-a456-426614174000")
  private String path;

  @Schema(description = "Validation errors for specific fields")
  private Map<String, String> validationErrors;

  @Schema(description = "Additional error details")
  private Map<String, Object> details;
}
