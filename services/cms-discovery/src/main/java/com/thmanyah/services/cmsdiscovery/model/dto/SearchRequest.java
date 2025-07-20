package com.thmanyah.services.cmsdiscovery.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Search request parameters")
public class SearchRequest {

  @Schema(description = "Search query text", example = "artificial intelligence")
  @Size(max = 500, message = "Query must not exceed 500 characters")
  private String query;

  @Schema(
      description = "Show type filter",
      example = "podcast",
      allowableValues = {"podcast", "documentary"})
  private String type;

  @Schema(description = "Language filter", example = "en")
  @Size(max = 10, message = "Language code must not exceed 10 characters")
  private String language;

  @Schema(description = "Provider filter", example = "youtube")
  @Size(max = 50, message = "Provider must not exceed 50 characters")
  private String provider;

  @Schema(description = "Tags filter", example = "[\"technology\", \"ai\"]")
  private List<String> tags;

  @Schema(description = "Categories filter", example = "[\"Science\", \"Technology\"]")
  private List<String> categories;

  @Schema(description = "Minimum duration in seconds", example = "300")
  @Min(value = 0, message = "Minimum duration must be non-negative")
  private Integer minDuration;

  @Schema(description = "Maximum duration in seconds", example = "7200")
  @Min(value = 0, message = "Maximum duration must be non-negative")
  private Integer maxDuration;

  @Schema(description = "Published after date", example = "2024-01-01")
  private LocalDate publishedAfter;

  @Schema(description = "Published before date", example = "2024-12-31")
  private LocalDate publishedBefore;

  @Schema(description = "Minimum rating", example = "4.0")
  @Min(value = 0, message = "Minimum rating must be non-negative")
  @Max(value = 5, message = "Maximum rating cannot exceed 5")
  private Double minRating;

  @Schema(
      description = "Sort field",
      example = "createdDate",
      allowableValues = {"relevance", "createdDate", "publishedAt", "viewCount", "rating", "title"})
  @Builder.Default
  private String sortBy = "relevance";

  @Schema(
      description = "Sort direction",
      example = "desc",
      allowableValues = {"asc", "desc"})
  @Builder.Default
  private String sortDirection = "desc";

  @Schema(description = "Page number (0-based)", example = "0")
  @Min(value = 0, message = "Page number must be non-negative")
  @Builder.Default
  private Integer page = 0;

  @Schema(description = "Page size", example = "20")
  @Min(value = 1, message = "Page size must be at least 1")
  @Max(value = 100, message = "Page size cannot exceed 100")
  @Builder.Default
  private Integer size = 20;

  @Schema(description = "Enable highlighting", example = "true")
  @Builder.Default
  private Boolean highlight = true;

  @Schema(description = "Enable fuzzy search", example = "true")
  @Builder.Default
  private Boolean fuzzy = true;
}
