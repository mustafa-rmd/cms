package com.thmanyah.services.cms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Show data transfer object")
public class ShowDto {

  @Schema(
      description = "Unique identifier of the show",
      example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID id;

  @Schema(
      description = "Type of the show",
      example = "podcast",
      allowableValues = {"podcast", "documentary"})
  private String type;

  @Schema(description = "Title of the show", example = "The Tech Talk Show")
  private String title;

  @Schema(
      description = "Detailed description of the show",
      example = "A weekly podcast about technology trends")
  private String description;

  @Schema(description = "Language of the show", example = "en")
  private String language;

  @Schema(description = "Duration of the show in seconds", example = "3600")
  private Integer durationSec;

  @Schema(description = "Date when the show was published", example = "2024-01-15")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedAt;

  @Schema(description = "Date and time when the show was created", example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdDate;

  @Schema(
      description = "Date and time when the show was last updated",
      example = "2024-01-15T10:30:00")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedDate;

  @Schema(description = "Email of the user who created the show", example = "user@example.com")
  private String createdBy;

  @Schema(description = "Email of the user who last updated the show", example = "user@example.com")
  private String updatedBy;

  @Schema(
      description = "Provider of the show data source",
      example = "internal",
      allowableValues = {"internal", "vimeo", "youtube"})
  private String provider;

  // Constructor for repository projections
  public ShowDto(
      UUID id,
      String type,
      String title,
      String description,
      String language,
      Integer durationSec,
      LocalDate publishedAt,
      LocalDateTime createdDate,
      LocalDateTime updatedDate,
      String createdBy,
      String updatedBy,
      String provider) {
    this.id = id;
    this.type = type;
    this.title = title;
    this.description = description;
    this.language = language;
    this.durationSec = durationSec;
    this.publishedAt = publishedAt;
    this.createdDate = createdDate;
    this.updatedDate = updatedDate;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    this.provider = provider;
  }
}
