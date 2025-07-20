package com.thmanyah.services.cmsdiscovery.controllers;

import com.thmanyah.services.cmsdiscovery.model.dto.SearchRequest;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchResponse;
import com.thmanyah.services.cmsdiscovery.model.dto.ShowSearchDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Search", description = "Content search and discovery operations")
public interface SearchControllerV1 {

  @Operation(
      summary = "Search shows",
      description = "Search shows with advanced filtering and sorting")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
      })
  ResponseEntity<SearchResponse> searchShows(
      @Parameter(description = "Search query text", example = "artificial intelligence")
          String query,
      @Parameter(description = "Show type filter", example = "podcast") String type,
      @Parameter(description = "Language filter", example = "en") String language,
      @Parameter(description = "Provider filter", example = "youtube") String provider,
      @Parameter(description = "Tags filter (comma-separated)", example = "technology,ai")
          String tags,
      @Parameter(
              description = "Categories filter (comma-separated)",
              example = "Science,Technology")
          String categories,
      @Parameter(description = "Minimum duration in seconds", example = "300") Integer minDuration,
      @Parameter(description = "Maximum duration in seconds", example = "7200") Integer maxDuration,
      @Parameter(description = "Published after date (yyyy-MM-dd)", example = "2024-01-01")
          String publishedAfter,
      @Parameter(description = "Published before date (yyyy-MM-dd)", example = "2024-12-31")
          String publishedBefore,
      @Parameter(description = "Minimum rating", example = "4.0") Double minRating,
      @Parameter(description = "Sort field", example = "createdDate") String sortBy,
      @Parameter(description = "Sort direction", example = "desc") String sortDirection,
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size,
      @Parameter(description = "Enable highlighting", example = "true") Boolean highlight,
      @Parameter(description = "Enable fuzzy search", example = "true") Boolean fuzzy);

  @Operation(summary = "Advanced search", description = "Advanced search with request body")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search request")
      })
  ResponseEntity<SearchResponse> advancedSearch(
      @Parameter(description = "Advanced search request") SearchRequest searchRequest);

  @Operation(summary = "Get show by ID", description = "Get a specific show by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Show found",
            content = @Content(schema = @Schema(implementation = ShowSearchDto.class))),
        @ApiResponse(responseCode = "404", description = "Show not found")
      })
  ResponseEntity<ShowSearchDto> getShowById(
      @Parameter(description = "Show ID", example = "123e4567-e89b-12d3-a456-426614174000")
          UUID showId);

  @Operation(summary = "Get popular shows", description = "Get popular shows ordered by view count")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Popular shows retrieved successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
      })
  ResponseEntity<SearchResponse> getPopularShows(
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size);

  @Operation(summary = "Get recent shows", description = "Get recently created shows")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent shows retrieved successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
      })
  ResponseEntity<SearchResponse> getRecentShows(
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size);

  @Operation(summary = "Get shows by type", description = "Get shows filtered by type")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Shows retrieved successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
      })
  ResponseEntity<SearchResponse> getShowsByType(
      @Parameter(description = "Show type", example = "podcast") String type,
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size);

  @Operation(summary = "Get shows by tag", description = "Get shows filtered by tag")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Shows retrieved successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
      })
  ResponseEntity<SearchResponse> getShowsByTag(
      @Parameter(description = "Tag name", example = "technology") String tag,
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size);

  @Operation(summary = "Get shows by category", description = "Get shows filtered by category")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Shows retrieved successfully",
            content = @Content(schema = @Schema(implementation = SearchResponse.class)))
      })
  ResponseEntity<SearchResponse> getShowsByCategory(
      @Parameter(description = "Category name", example = "Science") String category,
      @Parameter(description = "Page number (0-based)", example = "0") Integer page,
      @Parameter(description = "Page size", example = "20") Integer size);
}
