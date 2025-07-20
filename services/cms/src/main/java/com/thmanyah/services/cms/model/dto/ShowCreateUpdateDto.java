package com.thmanyah.services.cms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data transfer object for creating or updating a show")
public class ShowCreateUpdateDto {

  @NotBlank(message = "Type is required")
  @Pattern(
      regexp = "^(podcast|documentary)$",
      message = "Type must be either 'podcast' or 'documentary'")
  @Schema(
      description = "Type of the show",
      example = "podcast",
      allowableValues = {"podcast", "documentary"})
  private String type;

  @NotBlank(message = "Title is required")
  @Size(max = 255, message = "Title must not exceed 255 characters")
  @Schema(description = "Title of the show", example = "The Tech Talk Show")
  private String title;

  @Size(max = 5000, message = "Description must not exceed 5000 characters")
  @Schema(
      description = "Detailed description of the show",
      example = "A weekly podcast about technology trends")
  private String description;

  @Pattern(
      regexp = "^[a-z]{2}$",
      message = "Language must be a valid 2-letter ISO 639-1 language code")
  @Schema(description = "Language of the show (ISO 639-1 code)", example = "en")
  private String language;

  @Min(value = 1, message = "Duration must be at least 1 second")
  @Max(value = 86400, message = "Duration must not exceed 24 hours (86400 seconds)")
  @Schema(description = "Duration of the show in seconds", example = "3600")
  private Integer durationSec;

  @Schema(description = "Date when the show was published", example = "2024-01-15")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedAt;
}
