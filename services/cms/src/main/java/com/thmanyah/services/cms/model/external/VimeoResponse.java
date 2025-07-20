package com.thmanyah.services.cms.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VimeoResponse {

  private int total = 0;
  private int page;

  @JsonProperty("per_page")
  private int perPage;

  private List<VimeoVideo> data;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VimeoVideo {
    private String uri;
    private String name;
    private String description;
    private String link;
    private Integer duration;
    private String language;

    @JsonProperty("created_time")
    private OffsetDateTime createdTime;

    @JsonProperty("modified_time")
    private OffsetDateTime modifiedTime;

    @JsonProperty("release_time")
    private OffsetDateTime releaseTime;
  }
}
