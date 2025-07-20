package com.thmanyah.services.cms.controllers;

import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import com.thmanyah.services.cms.model.dto.ShowDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "Shows", description = "Show management operations")
public interface ShowControllerV1 {

  @Operation(summary = "Get all shows", description = "Retrieve all shows with pagination")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShows(
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(summary = "Get show by ID", description = "Retrieve a specific show by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved show",
            content = @Content(schema = @Schema(implementation = ShowDto.class))),
        @ApiResponse(responseCode = "404", description = "Show not found")
      })
  ResponseEntity<ShowDto> getShowById(
      @Parameter(description = "Show ID", example = "123e4567-e89b-12d3-a456-426614174000")
          UUID id);

  @Operation(summary = "Create a new show", description = "Create a new show")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Show created successfully",
            content = @Content(schema = @Schema(implementation = ShowDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Show with title already exists")
      })
  ResponseEntity<ShowDto> createShow(ShowCreateUpdateDto showCreateUpdateDto);

  @Operation(summary = "Update a show", description = "Update an existing show")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show updated successfully",
            content = @Content(schema = @Schema(implementation = ShowDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Show not found"),
        @ApiResponse(responseCode = "409", description = "Show with title already exists")
      })
  ResponseEntity<ShowDto> updateShow(
      @Parameter(description = "Show ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
      ShowCreateUpdateDto showCreateUpdateDto);

  @Operation(summary = "Delete a show", description = "Delete a show by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Show deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Show not found")
      })
  ResponseEntity<Void> deleteShow(
      @Parameter(description = "Show ID", example = "123e4567-e89b-12d3-a456-426614174000")
          UUID id);

  @Operation(summary = "Delete multiple shows", description = "Delete multiple shows by their IDs")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Shows deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Some shows not found")
      })
  ResponseEntity<Void> deleteShows(Set<UUID> ids);

  @Operation(summary = "Get shows by type", description = "Retrieve shows filtered by type")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShowsByType(
      @Parameter(description = "Show type", example = "podcast") String type,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(summary = "Get shows by language", description = "Retrieve shows filtered by language")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShowsByLanguage(
      @Parameter(description = "Language code", example = "en") String language,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(summary = "Get shows by provider", description = "Retrieve shows filtered by provider")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShowsByProvider(
      @Parameter(description = "Provider name (internal, vimeo, youtube)", example = "vimeo")
          String provider,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Search shows by title",
      description = "Search shows by title (case-insensitive)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> searchShowsByTitle(
      @Parameter(description = "Title to search for", example = "tech") String title,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Get shows by published date",
      description = "Retrieve shows published on a specific date")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShowsByPublishedAt(
      @Parameter(description = "Published date", example = "2024-01-15") LocalDate publishedAt,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Get shows by creator",
      description = "Retrieve shows created by a specific user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved shows",
            content = @Content(schema = @Schema(implementation = Page.class)))
      })
  ResponseEntity<Page<ShowDto>> getShowsByCreatedBy(
      @Parameter(description = "Creator email", example = "user@example.com") String userEmail,
      @Parameter(description = "Page number (0-based)", example = "0") int pageNumber,
      @Parameter(description = "Page size", example = "10") int pageSize);

  @Operation(
      summary = "Get distinct field values",
      description = "Get distinct values for filtering fields")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved distinct values",
            content = @Content(schema = @Schema(implementation = Map.class)))
      })
  ResponseEntity<Map<String, Map<String, Object>>> getDistinctFields();

  @Operation(summary = "Get show statistics", description = "Get statistics about shows")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved statistics",
            content = @Content(schema = @Schema(implementation = Map.class)))
      })
  ResponseEntity<Map<String, Object>> getShowStatistics();
}
