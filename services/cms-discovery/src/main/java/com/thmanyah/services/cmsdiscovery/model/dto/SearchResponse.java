package com.thmanyah.services.cmsdiscovery.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Search response with results and metadata")
public class SearchResponse {

  @Schema(description = "Search results")
  private List<ShowSearchDto> results;

  @Schema(description = "Total number of results", example = "150")
  private long totalResults;

  @Schema(description = "Total number of pages", example = "8")
  private int totalPages;

  @Schema(description = "Current page number", example = "0")
  private int currentPage;

  @Schema(description = "Page size", example = "20")
  private int pageSize;

  @Schema(description = "Whether there are more results", example = "true")
  private boolean hasNext;

  @Schema(description = "Whether there are previous results", example = "false")
  private boolean hasPrevious;

  @Schema(description = "Search execution time in milliseconds", example = "45")
  private long executionTimeMs;

  @Schema(description = "Applied filters")
  private Map<String, Object> appliedFilters;

  @Schema(description = "Search suggestions for query correction")
  private List<String> suggestions;

  @Schema(description = "Aggregation results (facets)")
  private Map<String, Map<String, Long>> aggregations;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Search metadata")
  public static class SearchMetadata {

    @Schema(description = "Original search query", example = "artificial intelligence")
    private String originalQuery;

    @Schema(description = "Processed search query", example = "artificial intelligence")
    private String processedQuery;

    @Schema(description = "Search type", example = "full_text")
    private String searchType;

    @Schema(description = "Whether fuzzy search was applied", example = "true")
    private boolean fuzzyApplied;

    @Schema(description = "Whether highlighting was applied", example = "true")
    private boolean highlightApplied;
  }

  @Schema(description = "Search metadata")
  private SearchMetadata metadata;
}
