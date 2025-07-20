package com.thmanyah.services.cmsdiscovery.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@elasticsearchIndexName}")
public class ShowDocument {

  @Id private String id;

  @Field(type = FieldType.Keyword)
  private UUID showId;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String title;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String description;

  @Field(type = FieldType.Keyword)
  private String type; // podcast, documentary

  @Field(type = FieldType.Keyword)
  private String language;

  @Field(type = FieldType.Integer)
  private Integer durationSec;

  @Field(
      type = FieldType.Date,
      format = {},
      pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedAt;

  @Field(type = FieldType.Keyword)
  private String provider; // internal, vimeo, youtube

  @Field(type = FieldType.Text)
  private String externalId;

  @Field(type = FieldType.Object)
  private List<String> tags;

  @Field(type = FieldType.Object)
  private List<String> categories;

  @Field(type = FieldType.Text)
  private String thumbnailUrl;

  @Field(type = FieldType.Text)
  private String streamUrl;

  @Field(type = FieldType.Boolean)
  private Boolean isPublic;

  @Field(type = FieldType.Boolean)
  private Boolean isActive;

  @Field(
      type = FieldType.Date,
      format = {},
      pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdDate;

  @Field(
      type = FieldType.Date,
      format = {},
      pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedDate;

  @Field(type = FieldType.Text)
  private String createdBy;

  @Field(type = FieldType.Text)
  private String updatedBy;

  // Search-optimized fields
  @Field(type = FieldType.Text, analyzer = "keyword")
  private String titleKeyword;

  @Field(type = FieldType.Text, analyzer = "simple")
  private String searchText; // Combined searchable text

  @Field(type = FieldType.Integer)
  private Integer viewCount;

  @Field(type = FieldType.Double)
  private Double rating;

  @Field(
      type = FieldType.Date,
      format = {},
      pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime indexedAt;

  /** Generate search text from title and description */
  public void generateSearchText() {
    StringBuilder searchTextBuilder = new StringBuilder();

    if (title != null) {
      searchTextBuilder.append(title).append(" ");
    }

    if (description != null) {
      searchTextBuilder.append(description).append(" ");
    }

    if (tags != null && !tags.isEmpty()) {
      searchTextBuilder.append(String.join(" ", tags)).append(" ");
    }

    if (categories != null && !categories.isEmpty()) {
      searchTextBuilder.append(String.join(" ", categories));
    }

    this.searchText = searchTextBuilder.toString().trim();
    this.titleKeyword = title;
    this.indexedAt = LocalDateTime.now();
  }
}
