package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.config.ExternalProviderProperties;
import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.external.VimeoOAuthResponse;
import com.thmanyah.services.cms.services.VimeoOAuthService;
import java.time.Duration;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class VimeoOAuthServiceImpl implements VimeoOAuthService {

  private static final String PROVIDER_NAME = "vimeo";
  private final ExternalProviderProperties properties;
  private final WebClient webClient;

  public VimeoOAuthServiceImpl(ExternalProviderProperties properties) {
    this.properties = properties;
    this.webClient = WebClient.builder().baseUrl(properties.getVimeo().getBaseUrl()).build();
  }

  @Override
  public String getAccessToken() {
    log.debug("Generating new Vimeo access token");

    try {
      // Create Basic Auth header
      String credentials =
          properties.getVimeo().getClientId() + ":" + properties.getVimeo().getClientSecret();
      String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

      // OAuth2 client credentials request
      String requestBody = "{\"grant_type\":\"client_credentials\",\"scope\":\"public\"}";

      VimeoOAuthResponse response =
          webClient
              .post()
              .uri("/oauth/authorize/client")
              .header("Authorization", basicAuth)
              .header("Content-Type", "application/json")
              .bodyValue(requestBody)
              .retrieve()
              .bodyToMono(VimeoOAuthResponse.class)
              .timeout(Duration.ofMillis(properties.getVimeo().getTimeout()))
              .block();

      if (response == null || response.getAccessToken() == null) {
        throw new ExternalProviderException(
            PROVIDER_NAME, "Failed to get access token from Vimeo OAuth response");
      }

      log.debug("Successfully generated Vimeo access token");
      return response.getAccessToken();

    } catch (WebClientResponseException e) {
      log.error("Vimeo OAuth error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw new ExternalProviderException(
          PROVIDER_NAME,
          "Vimeo OAuth error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
          e);
    } catch (Exception e) {
      log.error("Failed to get Vimeo access token", e);
      throw new ExternalProviderException(
          PROVIDER_NAME, "Failed to get access token: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean isConfigured() {
    return properties.getVimeo().getClientId() != null
        && !properties.getVimeo().getClientId().trim().isEmpty()
        && properties.getVimeo().getClientSecret() != null
        && !properties.getVimeo().getClientSecret().trim().isEmpty();
  }
}
