package com.thmanyah.services.cms.adapters;

import com.thmanyah.services.cms.config.ExternalProviderProperties;
import com.thmanyah.services.cms.exception.ExternalProviderException;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.ports.ExternalProviderPort;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("mock")
@Slf4j
@ConditionalOnProperty(
    name = "external-providers.mock.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class MockImportProvider implements ExternalProviderPort {

  private static final String PROVIDER_NAME = "mock";
  private final Random random = new Random();
  private final ExternalProviderProperties properties;

  public MockImportProvider(ExternalProviderProperties properties) {
    this.properties = properties;
  }

  @Override
  public List<ShowExternalDto> fetch(String topic, LocalDate startDate, LocalDate endDate) {
    log.info("Fetching mock content from {} to {}", startDate, endDate);

    try {
      // Simulate API call delay
      Thread.sleep(1000 + random.nextInt(2000));

      // Mock data generation
      return generateMockData(startDate, endDate);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ExternalProviderException(PROVIDER_NAME, "Fetch operation was interrupted", e);
    } catch (Exception e) {
      throw new ExternalProviderException(PROVIDER_NAME, "Failed to fetch content", e);
    }
  }

  @Override
  public String getProviderName() {
    return PROVIDER_NAME;
  }

  @Override
  public boolean isAvailable() {
    // Mock provider is always available when enabled
    return properties.getMock().isEnabled();
  }

  @Override
  public int getMaxBatchSize() {
    return 25; // Mock provider batch size
  }

  @Override
  public int getRateLimitPerMinute() {
    return 120; // Mock provider rate limit
  }

  private List<ShowExternalDto> generateMockData(LocalDate startDate, LocalDate endDate) {
    log.debug("Generating mock data between {} and {}", startDate, endDate);

    return List.of(
        ShowExternalDto.withExternalInfo(
            "podcast",
            "Tech Talk: AI Revolution",
            "Deep dive into artificial intelligence and its impact on society. "
                + "Featuring interviews with leading AI researchers and industry experts.",
            "en",
            3600,
            startDate.plusDays(1),
            "mock_ai_revolution_123",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "documentary",
            "The History of Oil: Black Gold",
            "A comprehensive documentary exploring the discovery, extraction, and "
                + "global impact of petroleum throughout human history.",
            "en",
            5400,
            startDate.plusDays(2),
            "mock_oil_history_456",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "podcast",
            "Startup Stories: From Garage to IPO",
            "Inspiring stories of entrepreneurs who built successful companies "
                + "from humble beginnings.",
            "en",
            2700,
            startDate.plusDays(3),
            "mock_startup_stories_789",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "documentary",
            "Climate Change: The Tipping Point",
            "Scientific analysis of climate change effects and potential solutions "
                + "for a sustainable future.",
            "en",
            4800,
            endDate.minusDays(2),
            "mock_climate_change_101",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "podcast",
            "الثقافة العربية في العصر الرقمي",
            "نقاش حول تأثير التكنولوجيا على الثقافة العربية والهوية في العصر الحديث",
            "ar",
            3300,
            endDate.minusDays(1),
            "mock_arabic_culture_202",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "documentary",
            "Space Exploration: Mars Mission",
            "Documentary about the latest Mars exploration missions and future plans "
                + "for human colonization of the red planet.",
            "en",
            4200,
            startDate.plusDays(4),
            "mock_mars_mission_303",
            PROVIDER_NAME),
        ShowExternalDto.withExternalInfo(
            "podcast",
            "Cybersecurity in 2024",
            "Discussion about the latest cybersecurity threats and protection strategies "
                + "for individuals and organizations.",
            "en",
            2850,
            startDate.plusDays(5),
            "mock_cybersecurity_404",
            PROVIDER_NAME));
  }
}
