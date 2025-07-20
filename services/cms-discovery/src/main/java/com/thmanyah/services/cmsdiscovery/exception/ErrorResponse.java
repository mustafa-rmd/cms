package com.thmanyah.services.cmsdiscovery.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response")
public class ErrorResponse {

  @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code", example = "400")
  private int status;

  @Schema(description = "Error type", example = "Bad Request")
  private String error;

  @Schema(description = "Error message", example = "Invalid search parameters")
  private String message;

  @Schema(description = "Request path", example = "/api/v1/search")
  private String path;
}
