package com.thmanyah.services.cmsdiscovery.services;

import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.event.ShowCreatedEvent;
import com.thmanyah.services.cmsdiscovery.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShowEventConsumerService {

  private final ShowRepository showRepository;

  /** Handle show created events from CMS service */
  @RabbitListener(queues = "${cms-discovery.rabbitmq.queue}")
  public void handleShowCreatedEvent(ShowCreatedEvent event) {
    try {
      log.info("Received show created event for show ID: {}", event.getShowId());

      // Check if show already exists
      if (showRepository.findByShowId(event.getShowId()).isPresent()) {
        log.warn("Show with ID {} already exists in Elasticsearch, skipping", event.getShowId());
        return;
      }

      // Convert event to document
      ShowDocument document = convertEventToDocument(event);

      // Generate search-optimized fields
      document.generateSearchText();

      // Save to Elasticsearch
      ShowDocument savedDocument = showRepository.save(document);

      log.info(
          "Successfully indexed show '{}' with ID: {} in Elasticsearch",
          savedDocument.getTitle(),
          savedDocument.getShowId());

    } catch (Exception e) {
      log.error("Failed to process show created event for show ID: {}", event.getShowId(), e);
      throw e; // Re-throw to trigger retry mechanism
    }
  }

  /** Convert ShowCreatedEvent to ShowDocument */
  private ShowDocument convertEventToDocument(ShowCreatedEvent event) {
    return ShowDocument.builder()
        .showId(event.getShowId())
        .title(event.getTitle())
        .description(event.getDescription())
        .type(event.getType())
        .language(event.getLanguage())
        .durationSec(event.getDurationSec())
        .publishedAt(event.getPublishedAt())
        .provider(event.getProvider())
        .externalId(event.getExternalId())
        .tags(event.getTags())
        .categories(event.getCategories())
        .thumbnailUrl(event.getThumbnailUrl())
        .streamUrl(event.getStreamUrl())
        .isPublic(event.getIsPublic())
        .isActive(event.getIsActive())
        .createdDate(event.getCreatedDate())
        .updatedDate(event.getUpdatedDate())
        .createdBy(event.getCreatedBy())
        .updatedBy(event.getUpdatedBy())
        // Initialize search-specific fields
        .viewCount(0)
        .rating(0.0)
        .build();
  }
}
