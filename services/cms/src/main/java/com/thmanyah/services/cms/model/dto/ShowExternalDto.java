package com.thmanyah.services.cms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(description = "External show data transfer object for importing from external providers")
public record ShowExternalDto(
    @NotBlank(message = "Type is required")
        @Pattern(
            regexp = "^(podcast|documentary)$",
            message = "Type must be either 'podcast' or 'documentary'")
        @Schema(
            description = "Type of the show",
            example = "podcast",
            allowableValues = {"podcast", "documentary"})
        String type,
    @NotBlank(message = "Title is required")
        @Schema(description = "Title of the show", example = "The Tech Talk Show")
        String title,
    @Schema(
            description = "Description of the show",
            example = "A weekly podcast about technology trends")
        String description,
    @Pattern(
            regexp = "^[a-z]{2}$",
            message = "Language must be a valid 2-letter ISO 639-1 language code")
        @Schema(description = "Language of the show (ISO 639-1 code)", example = "en")
        String language,
    @Positive(message = "Duration must be positive")
        @Schema(description = "Duration of the show in seconds", example = "3600")
        Integer durationSec,
    @NotNull(message = "Published date is required")
        @Schema(description = "Date when the show was published", example = "2024-01-15")
        LocalDate publishedAt,
    @Schema(description = "External provider identifier", example = "youtube_video_123")
        String externalId,
    @Schema(description = "Source provider name", example = "youtube") String sourceProvider) {

  /** Create a ShowExternalDto with minimal required fields */
  public static ShowExternalDto of(
      String type,
      String title,
      String description,
      String language,
      Integer durationSec,
      LocalDate publishedAt) {
    return new ShowExternalDto(
        type, title, description, language, durationSec, publishedAt, null, null);
  }

  /** Create a ShowExternalDto with external tracking information */
  public static ShowExternalDto withExternalInfo(
      String type,
      String title,
      String description,
      String language,
      Integer durationSec,
      LocalDate publishedAt,
      String externalId,
      String sourceProvider) {
    return new ShowExternalDto(
        type, title, description, language, durationSec, publishedAt, externalId, sourceProvider);
  }
}
