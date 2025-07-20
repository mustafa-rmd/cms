package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.event.ShowCreatedEvent;
import com.thmanyah.services.cms.services.EventPublisherService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

  private final RabbitTemplate rabbitTemplate;

  @Value("${cms.events.rabbitmq.routing-key.show-created}")
  private String showCreatedRoutingKey;

  @Value("${cms.events.rabbitmq.routing-key.show-updated}")
  private String showUpdatedRoutingKey;

  @Value("${cms.events.rabbitmq.routing-key.show-deleted}")
  private String showDeletedRoutingKey;

  /** Publish show created event */
  @Override
  public void publishShowCreatedEvent(Show show) {
    try {
      ShowCreatedEvent event = convertToEvent(show);

      log.info(
          "Publishing show created event for show ID: {} with title: '{}'",
          show.getId(),
          show.getTitle());

      rabbitTemplate.convertAndSend(showCreatedRoutingKey, event);

      log.info("Successfully published show created event for show ID: {}", show.getId());

    } catch (Exception e) {
      log.error("Failed to publish show created event for show ID: {}", show.getId(), e);
      // Don't throw exception to avoid breaking the main flow
    }
  }

  /** Publish show updated event */
  @Override
  public void publishShowUpdatedEvent(Show show) {
    try {
      ShowCreatedEvent event = convertToEvent(show);
      event.setEventType("SHOW_UPDATED");

      log.info(
          "Publishing show updated event for show ID: {} with title: '{}'",
          show.getId(),
          show.getTitle());

      rabbitTemplate.convertAndSend(showUpdatedRoutingKey, event);

      log.info("Successfully published show updated event for show ID: {}", show.getId());

    } catch (Exception e) {
      log.error("Failed to publish show updated event for show ID: {}", show.getId(), e);
    }
  }

  /** Publish show deleted event */
  @Override
  public void publishShowDeletedEvent(Show show) {
    try {
      ShowCreatedEvent event = convertToEvent(show);
      event.setEventType("SHOW_DELETED");

      log.info(
          "Publishing show deleted event for show ID: {} with title: '{}'",
          show.getId(),
          show.getTitle());

      rabbitTemplate.convertAndSend(showDeletedRoutingKey, event);

      log.info("Successfully published show deleted event for show ID: {}", show.getId());

    } catch (Exception e) {
      log.error("Failed to publish show deleted event for show ID: {}", show.getId(), e);
    }
  }

  /** Publish show deleted event (legacy method for backward compatibility) */
  public void publishShowDeletedEvent(UUID showId, String title) {
    try {
      ShowCreatedEvent event =
          ShowCreatedEvent.builder()
              .showId(showId)
              .title(title)
              .eventTimestamp(LocalDateTime.now())
              .eventId(UUID.randomUUID().toString())
              .eventType("SHOW_DELETED")
              .eventSource("cms-service")
              .build();

      log.info("Publishing show deleted event for show ID: {} with title: '{}'", showId, title);

      rabbitTemplate.convertAndSend(showDeletedRoutingKey, event);

      log.info("Successfully published show deleted event for show ID: {}", showId);

    } catch (Exception e) {
      log.error("Failed to publish show deleted event for show ID: {}", showId, e);
    }
  }

  /** Convert Show entity to ShowCreatedEvent */
  private ShowCreatedEvent convertToEvent(Show show) {
    return ShowCreatedEvent.builder()
        .showId(show.getId())
        .title(show.getTitle())
        .description(show.getDescription())
        .type(show.getType())
        .language(show.getLanguage())
        .durationSec(show.getDurationSec())
        .publishedAt(show.getPublishedAt())
        .provider(show.getProvider())
        .externalId(null) // Not available in current Show entity
        .tags(null) // Not available in current Show entity
        .categories(null) // Not available in current Show entity
        .thumbnailUrl(null) // Not available in current Show entity
        .streamUrl(null) // Not available in current Show entity
        .isPublic(true) // Default to public for now
        .isActive(true) // Default to active for now
        .createdDate(show.getCreatedDate())
        .updatedDate(show.getUpdatedDate())
        .createdBy(show.getCreatedBy())
        .updatedBy(show.getUpdatedBy())
        .eventTimestamp(LocalDateTime.now())
        .eventId(UUID.randomUUID().toString())
        .build();
  }
}
