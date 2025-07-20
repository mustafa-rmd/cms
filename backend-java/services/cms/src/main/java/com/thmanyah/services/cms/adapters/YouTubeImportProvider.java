package com.thmanyah.services.cms.adapters;

import com.thmanyah.services.cms.config.ExternalProviderProperties;
import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.model.external.YouTubeResponse;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component("youtube")
@Slf4j
@ConditionalOnProperty(name = "external-providers.youtube.api-key")
public class YouTubeImportProvider implements ExternalProviderPort {

  private static final String PROVIDER_NAME = "youtube";
  private final ExternalProviderProperties properties;
  private final WebClient webClient;

  public YouTubeImportProvider(ExternalProviderProperties properties) {
    this.properties = properties;
    this.webClient = WebClient.builder().baseUrl(properties.getYoutube().getBaseUrl()).build();
  }

  @Override
  public List<ShowExternalDto> fetch(String topic, LocalDate startDate, LocalDate endDate) {
    log.info(
        "Fetching YouTube content from {} to {} for topic: '{}'",
        startDate,
        endDate,
        topic);

    validateConfiguration();

    try {
      String publishedAfter =
          startDate.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";
      String publishedBefore =
          endDate.atTime(23, 59, 59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z";

      String url =
          "/search"
              + "?key="
              + properties.getYoutube().getApiKey()
              + "&q="
              + java.net.URLEncoder.encode(topic, java.nio.charset.StandardCharsets.UTF_8)
              + "&part=snippet,id"
              + "&type=video"
              + "&order=date"
              + "&maxResults="
              + properties.getYoutube().getMaxResults()
              + "&publishedAfter="
              + publishedAfter
              + "&publishedBefore="
              + publishedBefore;

      log.debug("YouTube API URL: {}", url.replace(properties.getYoutube().getApiKey(), "***"));

      YouTubeResponse response =
          webClient
              .get()
              .uri(url)
              .retrieve()
              .bodyToMono(YouTubeResponse.class)
              .timeout(Duration.ofMillis(properties.getYoutube().getTimeout()))
              .block();

      return convertToShowExternalDtos(response);

    } catch (WebClientResponseException e) {
      log.error("YouTube API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
      throw new ExternalProviderException(
          PROVIDER_NAME,
          "YouTube API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
          e);
    } catch (Exception e) {
      log.error("Failed to fetch YouTube content", e);
      throw new ExternalProviderException(
          PROVIDER_NAME, "Failed to fetch content: " + e.getMessage(), e);
    }
  }

  @Override
  public String getProviderName() {
    return PROVIDER_NAME;
  }

  @Override
  public boolean isAvailable() {
    return StringUtils.hasText(properties.getYoutube().getApiKey());
  }

  @Override
  public int getMaxBatchSize() {
    return properties.getYoutube().getMaxResults();
  }

  @Override
  public int getRateLimitPerMinute() {
    return 100; // YouTube API quota limit (approximate)
  }

  private void validateConfiguration() {
    if (!StringUtils.hasText(properties.getYoutube().getApiKey())) {
      throw new ExternalProviderException(PROVIDER_NAME, "YouTube API key is not configured");
    }
  }

  private List<ShowExternalDto> convertToShowExternalDtos(YouTubeResponse response) {
    if (response == null || response.getItems() == null) {
      log.warn("Empty response from YouTube API");
      return List.of();
    }

    List<ShowExternalDto> shows = new ArrayList<>();

    for (YouTubeResponse.YouTubeItem item : response.getItems()) {
      try {
        ShowExternalDto show = convertYouTubeItemToShow(item);
        if (show != null) {
          shows.add(show);
        }
      } catch (Exception e) {
        log.warn("Failed to convert YouTube item to show: {}", e.getMessage());
      }
    }

    log.info("Converted {} YouTube videos to shows", shows.size());
    return shows;
  }

  private ShowExternalDto convertYouTubeItemToShow(YouTubeResponse.YouTubeItem item) {
    if (item.getSnippet() == null) {
      return null;
    }

    var snippet = item.getSnippet();

    // Determine type based on title/description keywords
    String type = determineShowType(snippet.getTitle(), snippet.getDescription());

    // Extract language (default to English if not specified)
    String language = "en"; // YouTube doesn't provide language in search API

    // Estimate duration (YouTube search API doesn't provide duration)
    // We'll use a default and note that this would need the videos API for accurate duration
    Integer durationSec = estimateDuration(snippet.getTitle(), snippet.getDescription());

    return ShowExternalDto.withExternalInfo(
        type,
        snippet.getTitle(),
        snippet.getDescription(),
        language,
        durationSec,
        snippet.getPublishedAt().toLocalDate(),
        item.getId().getVideoId(),
        PROVIDER_NAME);
  }

  private String determineShowType(String title, String description) {
    String combined = (title + " " + description).toLowerCase();

    // Keywords that suggest documentary
    if (combined.contains("documentary")
        || combined.contains("film")
        || combined.contains("history")
        || combined.contains("science")
        || combined.contains("nature")
        || combined.contains("biography")) {
      return "documentary";
    }

    // Default to podcast for other content
    return "podcast";
  }

  private Integer estimateDuration(String title, String description) {
    String combined = (title + " " + description).toLowerCase();

    // Look for duration hints in title/description
    if (combined.contains("short") || combined.contains("clip")) {
      return 300; // 5 minutes
    } else if (combined.contains("full")
        || combined.contains("complete")
        || combined.contains("documentary")) {
      return 3600; // 1 hour
    } else {
      return 1800; // 30 minutes default
    }
  }
}
