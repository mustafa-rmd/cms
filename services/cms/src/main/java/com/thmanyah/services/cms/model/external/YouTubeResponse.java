package com.thmanyah.services.cms.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeResponse {

  private String kind;
  private String etag;
  private String nextPageToken;
  private String prevPageToken;
  private PageInfo pageInfo;
  private List<YouTubeItem> items;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class PageInfo {
    private int totalResults;
    private int resultsPerPage;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class YouTubeItem {
    private String kind;
    private String etag;
    private Id id;
    private Snippet snippet;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Id {
      private String kind;
      private String videoId;
      private String channelId;
      private String playlistId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
      private LocalDateTime publishedAt;
      private String channelId;
      private String title;
      private String description;
      private Thumbnails thumbnails;
      private String channelTitle;
      private String liveBroadcastContent;
      private LocalDateTime publishTime;

      @Data
      @JsonIgnoreProperties(ignoreUnknown = true)
      public static class Thumbnails {
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;

        private Thumbnail medium;
        private Thumbnail high;
        private Thumbnail standard;
        private Thumbnail maxres;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Thumbnail {
          private String url;
          private int width;
          private int height;
        }
      }
    }
  }
}
