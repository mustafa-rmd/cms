package com.thmanyah.services.cmsdiscovery.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Show search result")
public class ShowSearchDto {

  @Schema(description = "Show ID", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID showId;

  @Schema(description = "Show title", example = "The Future of AI")
  private String title;

  @Schema(
      description = "Show description",
      example = "An in-depth look at artificial intelligence...")
  private String description;

  @Schema(
      description = "Show type",
      example = "podcast",
      allowableValues = {"podcast", "documentary"})
  private String type;

  @Schema(description = "Language code", example = "en")
  private String language;

  @Schema(description = "Duration in seconds", example = "3600")
  private Integer durationSec;

  @Schema(description = "Published date", example = "2024-01-15")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedAt;

  @Schema(description = "Content provider", example = "youtube")
  private String provider;

  @Schema(description = "External provider ID", example = "dQw4w9WgXcQ")
  private String externalId;

  @Schema(description = "Content tags", example = "[\"technology\", \"ai\", \"future\"]")
  private List<String> tags;

  @Schema(description = "Content categories", example = "[\"Science\", \"Technology\"]")
  private List<String> categories;

  @Schema(description = "Thumbnail image URL", example = "https://example.com/thumbnail.jpg")
  private String thumbnailUrl;

  @Schema(description = "Stream URL", example = "https://example.com/stream.m3u8")
  private String streamUrl;

  @Schema(description = "View count", example = "1500")
  private Integer viewCount;

  @Schema(description = "Average rating", example = "4.5")
  private Double rating;

  @Schema(description = "Creation date", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdDate;

  @Schema(description = "Last update date", example = "2024-01-16T14:20:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedDate;

  @Schema(description = "Created by user", example = "editor@example.com")
  private String createdBy;

  @Schema(description = "Last updated by user", example = "admin@example.com")
  private String updatedBy;

  // Search-specific fields
  @Schema(description = "Search relevance score", example = "0.95")
  private Float score;

  @Schema(description = "Highlighted text snippets")
  private List<String> highlights;
}
