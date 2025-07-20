package com.thmanyah.services.cms.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "shows",
    indexes = {
      @Index(name = "idx_shows_type", columnList = "type"),
      @Index(name = "idx_shows_language", columnList = "language"),
      @Index(name = "idx_shows_published_at", columnList = "publishedAt"),
      @Index(name = "idx_shows_created_date", columnList = "createdDate"),
      @Index(name = "idx_shows_title", columnList = "title"),
      @Index(name = "idx_shows_provider", columnList = "provider")
    })
public class Show {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false, length = 50)
  private String type; // podcast, documentary

  @Column(nullable = false, length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(length = 10)
  private String language;

  @Column(name = "duration_sec")
  private Integer durationSec;

  @Column(name = "published_at")
  private LocalDate publishedAt;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "updated_date", nullable = false)
  private LocalDateTime updatedDate;

  @Column(name = "created_by", length = 255)
  private String createdBy;

  @Column(name = "updated_by", length = 255)
  private String updatedBy;

  @Column(name = "provider", length = 50, nullable = false)
  private String
      provider; // "internal" for manually created shows, "vimeo", "youtube", etc. for imported

  // shows

  @PrePersist
  private void prePersist() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    this.createdDate = now;
    this.updatedDate = now;
  }

  @PreUpdate
  private void preUpdate() {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
    if (this.createdDate == null) {
      this.createdDate = now;
    }
    this.updatedDate = now;
  }
}
