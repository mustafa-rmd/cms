package com.thmanyah.services.cms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external-providers")
public class ExternalProviderProperties {

  private YouTube youtube = new YouTube();
  private Vimeo vimeo = new Vimeo();
  private Mock mock = new Mock();

  @Data
  public static class YouTube {
    private String apiKey;
    private String baseUrl = "https://www.googleapis.com/youtube/v3";
    private String channelId;
    private int maxResults = 50;
    private int timeout = 30000;
  }

  @Data
  public static class Vimeo {
    private String clientId;
    private String clientSecret;
    private String baseUrl = "https://api.vimeo.com";
    private String oauthUrl = "https://api.vimeo.com/oauth/authorize/client";
    private int maxResults = 50;
    private int timeout = 30000;
  }

  @Data
  public static class Mock {
    private boolean enabled = true;
  }
}
