package com.thmanyah.services.cms.adapters;

import com.thmanyah.services.cms.config.ExternalProviderProperties;
import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.model.external.VimeoResponse;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import com.thmanyah.services.cms.services.VimeoOAuthService;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component("vimeo")
@Slf4j
public class VimeoImportProvider implements ExternalProviderPort {

  private static final String PROVIDER_NAME = "vimeo";
  private final ExternalProviderProperties properties;
  private final VimeoOAuthService oauthService;
  private final WebClient webClient;

  public VimeoImportProvider(
      ExternalProviderProperties properties, VimeoOAuthService oauthService) {
    this.properties = properties;
    this.oauthService = oauthService;
    this.webClient =
        WebClient.builder()
            .baseUrl(properties.getVimeo().getBaseUrl())
            .codecs(
                configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB buffer
            .build();
  }

  @Override
  public List<ShowExternalDto> fetch(String topic, LocalDate startDate, LocalDate endDate) {
    log.info("Fetching Vimeo content from {} to {}", startDate, endDate);

    validateConfiguration();

    try {
      // Generate fresh access token for each request
      String accessToken = oauthService.getAccessToken();
      log.debug("Generated fresh Vimeo access token");

      // Use Vimeo's public videos search endpoint which is more accessible
      String url =
          "/videos"
              + "?query="
              + topic
              + "&per_page="
              + properties.getVimeo().getMaxResults()
              + "&sort=date"
              + "&direction=desc";

      log.debug("Vimeo API URL: {}", url);

      VimeoResponse response =
          webClient
              .get()
              .uri(url)
              .header("Authorization", "Bearer " + accessToken)
              .header("Accept", "application/vnd.vimeo.*+json;version=3.4")
              .retrieve()
              .bodyToMono(VimeoResponse.class)
              .timeout(Duration.ofMillis(properties.getVimeo().getTimeout()))
              .block();

      if (Objects.nonNull(response)) {
        log.info("Found matched videos  of count: {}", response.getTotal());
      }

      return convertToShowExternalDtos(response, startDate, endDate);

    } catch (WebClientResponseException e) {
      log.error("Vimeo API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw new ExternalProviderException(
          PROVIDER_NAME,
          "Vimeo API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
          e);
    } catch (org.springframework.core.io.buffer.DataBufferLimitException e) {
      log.error("Vimeo API response too large (buffer limit exceeded)");
      throw new ExternalProviderException(
          PROVIDER_NAME, "Vimeo API response too large - buffer limit exceeded", e);
    } catch (Exception e) {
      log.error("Failed to fetch Vimeo content", e);

      // Check if it's a buffer limit exception in the cause chain
      Throwable cause = e.getCause();
      while (cause != null) {
        if (cause instanceof org.springframework.core.io.buffer.DataBufferLimitException) {
          throw new ExternalProviderException(
              PROVIDER_NAME,
              "Vimeo API response too large - buffer limit exceeded in cause chain",
              e);
        }
        cause = cause.getCause();
      }

      throw new ExternalProviderException(
          PROVIDER_NAME, "Failed to fetch content from Vimeo API: " + e.getMessage(), e);
    }
  }

  @Override
  public String getProviderName() {
    return PROVIDER_NAME;
  }

  @Override
  public boolean isAvailable() {
    return oauthService.isConfigured();
  }

  private void validateConfiguration() {
    if (!oauthService.isConfigured()) {
      throw new ExternalProviderException(
          PROVIDER_NAME, "Vimeo OAuth credentials are not configured");
    }
  }

  @Override
  public int getMaxBatchSize() {
    return properties.getVimeo().getMaxResults();
  }

  @Override
  public int getRateLimitPerMinute() {
    return 60; // Vimeo API rate limit
  }

  private List<ShowExternalDto> convertToShowExternalDtos(
      VimeoResponse response, LocalDate startDate, LocalDate endDate) {
    if (response == null || response.getData() == null) {
      log.warn("Empty response from Vimeo API");
      return List.of();
    }

    List<ShowExternalDto> shows = new ArrayList<>();

    for (VimeoResponse.VimeoVideo video : response.getData()) {
      try {
        // Filter by date range since Vimeo API doesn't support date filtering on /me/videos
        ShowExternalDto show = convertVimeoVideoToShow(video);
        if (show != null) {
          shows.add(show);
        }
      } catch (Exception e) {
        log.warn("Failed to convert Vimeo video to show: {}", e.getMessage());
      }
    }

    log.info("Converted {} Vimeo videos to shows (filtered by date range)", shows.size());
    return shows;
  }

  private boolean isVideoInDateRange(
      VimeoResponse.VimeoVideo video, LocalDate startDate, LocalDate endDate) {
    if (video.getCreatedTime() == null) {
      return false;
    }

    LocalDate videoDate = video.getCreatedTime().toLocalDate();
    return !videoDate.isBefore(startDate) && !videoDate.isAfter(endDate);
  }

  private ShowExternalDto convertVimeoVideoToShow(VimeoResponse.VimeoVideo video) {
    if (video.getName() == null) {
      return null;
    }

    // Determine type based on title/description keywords
    String type = determineShowType(video.getName(), video.getDescription());

    // Extract language (default to English if not specified)
    String language = video.getLanguage() != null ? video.getLanguage() : "en";

    // Use actual duration from Vimeo API (default to 0 if null)
    Integer durationSec = video.getDuration() != null ? video.getDuration() : 0;

    // Extract video ID from URI (format: /videos/123456789)
    String externalId = extractVideoId(video.getUri());

    // Use created time for published date
    LocalDate publishedAt =
        video.getCreatedTime() != null ? video.getCreatedTime().toLocalDate() : LocalDate.now();

    return ShowExternalDto.withExternalInfo(
        type,
        video.getName(),
        video.getDescription() != null ? video.getDescription() : "",
        language,
        durationSec,
        publishedAt,
        externalId,
        PROVIDER_NAME);
  }

  private String determineShowType(String title, String description) {
    String combined = (title + " " + (description != null ? description : "")).toLowerCase();

    // Keywords that suggest documentary
    if (combined.contains("documentary")
        || combined.contains("film")
        || combined.contains("history")
        || combined.contains("science")
        || combined.contains("nature")
        || combined.contains("biography")
        || combined.contains("education")
        || combined.contains("learning")) {
      return "documentary";
    }

    // Default to podcast for other content
    return "podcast";
  }

  private String extractVideoId(String uri) {
    if (uri == null) {
      return null;
    }
    // URI format: /videos/123456789
    String[] parts = uri.split("/");
    return parts.length > 2 ? parts[2] : uri;
  }
}
