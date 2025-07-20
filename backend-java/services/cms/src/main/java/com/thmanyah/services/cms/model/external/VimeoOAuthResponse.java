package com.thmanyah.services.cms.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VimeoOAuthResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("token_type")
  private String tokenType;

  private String scope;

  private App app;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class App {
    private String name;
    private String uri;
  }
}
