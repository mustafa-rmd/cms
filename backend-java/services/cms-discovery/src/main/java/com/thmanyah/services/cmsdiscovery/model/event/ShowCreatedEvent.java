package com.thmanyah.services.cmsdiscovery.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ShowCreatedEvent {

  private UUID showId;
  private String title;
  private String description;
  private String type; // podcast, documentary
  private String language;
  private Integer durationSec;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedAt;

  private String provider; // internal, vimeo, youtube
  private String externalId;
  private List<String> tags;
  private List<String> categories;
  private String thumbnailUrl;
  private String streamUrl;
  private Boolean isPublic;
  private Boolean isActive;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedDate;

  private String createdBy;
  private String updatedBy;

  // Event metadata
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime eventTimestamp;

  private String eventId;

  @Builder.Default private String eventType = "SHOW_CREATED";

  @Builder.Default private String eventSource = "cms-service";
}
