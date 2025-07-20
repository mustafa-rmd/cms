package com.thmanyah.services.cms.controllers;

import com.thmanyah.services.cms.model.ImportJob;
import com.thmanyah.services.cms.model.dto.ImportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Import", description = "Import operations for external content providers")
@SecurityRequirement(name = "Bearer Authentication")
public interface ImportControllerV1 {

  @Operation(
      summary = "Import shows from external provider (DEPRECATED)",
      description =
          "⚠️ DEPRECATED: Use /api/v1/import/async/{provider} instead. "
              + "This synchronous endpoint blocks the request until completion and may timeout for large imports. "
              + "The async endpoint provides better performance, progress tracking, and fault tolerance.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Import job completed successfully (synchronous)",
            content = @Content(schema = @Schema(implementation = ImportJob.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Provider not found"),
        @ApiResponse(responseCode = "503", description = "Provider unavailable"),
        @ApiResponse(responseCode = "408", description = "Request timeout for large imports")
      })
  @Deprecated
  ResponseEntity<ImportJob> importFromProvider(
      @Parameter(description = "Provider name", example = "youtube") String provider,
      ImportRequest request);

  @Operation(
      summary = "Get import job status",
      description = "Retrieve the status and details of an import job")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Import job details retrieved successfully",
            content = @Content(schema = @Schema(implementation = ImportJob.class))),
        @ApiResponse(responseCode = "404", description = "Import job not found")
      })
  ResponseEntity<ImportJob> getImportJobStatus(
      @Parameter(description = "Import job ID", example = "123e4567-e89b-12d3-a456-426614174000")
          UUID jobId);

  @Operation(
      summary = "Cancel import job",
      description = "Cancel an import job that is currently in progress")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Import job cancelled successfully",
            content = @Content(schema = @Schema(implementation = ImportJob.class))),
        @ApiResponse(responseCode = "400", description = "Cannot cancel completed job"),
        @ApiResponse(responseCode = "404", description = "Import job not found")
      })
  ResponseEntity<ImportJob> cancelImportJob(
      @Parameter(description = "Import job ID", example = "123e4567-e89b-12d3-a456-426614174000")
          UUID jobId,
      @Parameter(description = "User email", example = "user@example.com") String userEmail);

  @Operation(
      summary = "Get all import jobs",
      description = "Retrieve all import jobs for monitoring purposes")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Import jobs retrieved successfully",
            content = @Content(schema = @Schema(implementation = List.class)))
      })
  ResponseEntity<List<ImportJob>> getAllImportJobs();

  @Operation(
      summary = "Get available providers",
      description = "Get list of available external content providers")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Available providers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Map.class)))
      })
  ResponseEntity<Map<String, Object>> getAvailableProviders();

  @Operation(
      summary = "Check provider health",
      description = "Check if a specific provider is available and healthy")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Provider health status retrieved",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Provider not found")
      })
  ResponseEntity<Map<String, Object>> checkProviderHealth(
      @Parameter(description = "Provider name", example = "youtube") String provider);
}
