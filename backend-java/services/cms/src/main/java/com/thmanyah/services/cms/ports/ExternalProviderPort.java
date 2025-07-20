package com.thmanyah.services.cms.ports;

import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import java.time.LocalDate;
import java.util.List;

/**
 * Port interface for external content providers. This follows the hexagonal architecture pattern,
 * allowing different implementations for various external sources (YouTube, Spotify, etc.)
 */
public interface ExternalProviderPort {

  /**
   * Fetch shows from external provider between the specified dates
   *
   * @param topic topic
   * @param startDate Start date (inclusive)
   * @param endDate End date (inclusive)
   * @return List of external show DTOs
   */
  List<ShowExternalDto> fetch(String topic, LocalDate startDate, LocalDate endDate);

  /**
   * Get the provider name/identifier
   *
   * @return Provider name (e.g., "youtube", "spotify")
   */
  String getProviderName();

  /**
   * Check if the provider is available/healthy
   *
   * @return true if provider is available, false otherwise
   */
  default boolean isAvailable() {
    return true;
  }

  /**
   * Get the maximum number of items this provider can fetch in one request
   *
   * @return Maximum batch size
   */
  default int getMaxBatchSize() {
    return 100;
  }

  /**
   * Get rate limit information (requests per minute)
   *
   * @return Rate limit per minute
   */
  default int getRateLimitPerMinute() {
    return 60;
  }
}
