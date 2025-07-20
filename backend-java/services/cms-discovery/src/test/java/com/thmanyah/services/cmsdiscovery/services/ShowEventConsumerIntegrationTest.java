package com.thmanyah.services.cmsdiscovery.services;

import com.thmanyah.services.cmsdiscovery.BaseIntegrationTest;
import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.event.ShowCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ShowEventConsumerService with RabbitMQ and Elasticsearch
 */
@DisplayName("Show Event Consumer Integration Tests")
public class ShowEventConsumerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should consume show created event and index in Elasticsearch")
    void shouldConsumeShowCreatedEventAndIndexInElasticsearch() throws InterruptedException {
        // Create show event
        ShowCreatedEvent event = createTestShowEvent("Technology Podcast", "Latest tech trends");
        
        // Publish event to RabbitMQ
        publishShowEvent(event);
        
        // Wait for message processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify show was indexed in Elasticsearch
        Optional<ShowDocument> indexedShow = showRepository.findByShowId(event.getShowId());
        
        assertThat(indexedShow).isPresent();
        assertThat(indexedShow.get().getTitle()).isEqualTo("Technology Podcast");
        assertThat(indexedShow.get().getDescription()).isEqualTo("Latest tech trends");
        assertThat(indexedShow.get().getShowId()).isEqualTo(event.getShowId());
        assertThat(indexedShow.get().getSearchText()).isNotNull();
        assertThat(indexedShow.get().getIndexedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle multiple show events")
    void shouldHandleMultipleShowEvents() throws InterruptedException {
        // Create multiple show events
        ShowCreatedEvent event1 = createTestShowEvent("Podcast 1", "First podcast");
        ShowCreatedEvent event2 = createTestShowEvent("Podcast 2", "Second podcast");
        ShowCreatedEvent event3 = createTestShowEvent("Documentary 1", "First documentary", "documentary", "en");
        
        // Publish events to RabbitMQ
        publishShowEvent(event1);
        publishShowEvent(event2);
        publishShowEvent(event3);
        
        // Wait for message processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify all shows were indexed
        assertThat(showRepository.count()).isEqualTo(3);
        
        Optional<ShowDocument> show1 = showRepository.findByShowId(event1.getShowId());
        Optional<ShowDocument> show2 = showRepository.findByShowId(event2.getShowId());
        Optional<ShowDocument> show3 = showRepository.findByShowId(event3.getShowId());
        
        assertThat(show1).isPresent();
        assertThat(show2).isPresent();
        assertThat(show3).isPresent();
        
        assertThat(show1.get().getTitle()).isEqualTo("Podcast 1");
        assertThat(show2.get().getTitle()).isEqualTo("Podcast 2");
        assertThat(show3.get().getTitle()).isEqualTo("Documentary 1");
        assertThat(show3.get().getType()).isEqualTo("documentary");
    }

    @Test
    @DisplayName("Should skip duplicate show events")
    void shouldSkipDuplicateShowEvents() throws InterruptedException {
        // Create show event
        ShowCreatedEvent event = createTestShowEvent("Duplicate Show", "This will be duplicated");
        
        // Publish the same event twice
        publishShowEvent(event);
        publishShowEvent(event);
        
        // Wait for message processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify only one show was indexed (duplicate was skipped)
        assertThat(showRepository.count()).isEqualTo(1);
        
        Optional<ShowDocument> indexedShow = showRepository.findByShowId(event.getShowId());
        assertThat(indexedShow).isPresent();
        assertThat(indexedShow.get().getTitle()).isEqualTo("Duplicate Show");
    }

    @Test
    @DisplayName("Should handle show event with all fields")
    void shouldHandleShowEventWithAllFields() throws InterruptedException {
        // Create comprehensive show event
        ShowCreatedEvent event = ShowCreatedEvent.builder()
                .showId(UUID.randomUUID())
                .title("Complete Show")
                .description("A show with all fields populated")
                .type("documentary")
                .language("es")
                .durationSec(7200)
                .publishedAt(LocalDate.now().minusDays(5))
                .provider("youtube")
                .externalId("yt-12345")
                .tags(List.of("technology", "science", "education"))
                .categories(List.of("Technology", "Science", "Education"))
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .streamUrl("https://example.com/stream.mp4")
                .isPublic(true)
                .isActive(true)
                .createdDate(LocalDateTime.now().minusDays(5))
                .updatedDate(LocalDateTime.now())
                .createdBy("admin@example.com")
                .updatedBy("editor@example.com")
                .eventId(UUID.randomUUID().toString())
                .eventType("SHOW_CREATED")
                .eventSource("cms-service")
                .eventTimestamp(LocalDateTime.now())
                .build();
        
        // Publish event
        publishShowEvent(event);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify all fields were mapped correctly
        Optional<ShowDocument> indexedShow = showRepository.findByShowId(event.getShowId());
        
        assertThat(indexedShow).isPresent();
        ShowDocument show = indexedShow.get();
        
        assertThat(show.getTitle()).isEqualTo("Complete Show");
        assertThat(show.getDescription()).isEqualTo("A show with all fields populated");
        assertThat(show.getType()).isEqualTo("documentary");
        assertThat(show.getLanguage()).isEqualTo("es");
        assertThat(show.getDurationSec()).isEqualTo(7200);
        assertThat(show.getPublishedAt()).isEqualTo(LocalDate.now().minusDays(5));
        assertThat(show.getProvider()).isEqualTo("youtube");
        assertThat(show.getExternalId()).isEqualTo("yt-12345");
        assertThat(show.getTags()).containsExactlyInAnyOrder("technology", "science", "education");
        assertThat(show.getCategories()).containsExactlyInAnyOrder("Technology", "Science", "Education");
        assertThat(show.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
        assertThat(show.getStreamUrl()).isEqualTo("https://example.com/stream.mp4");
        assertThat(show.getIsPublic()).isTrue();
        assertThat(show.getIsActive()).isTrue();
        assertThat(show.getCreatedBy()).isEqualTo("admin@example.com");
        assertThat(show.getUpdatedBy()).isEqualTo("editor@example.com");
        
        // Verify search text was generated
        assertThat(show.getSearchText()).isNotNull();
        assertThat(show.getSearchText()).contains("Complete Show");
        assertThat(show.getSearchText()).contains("technology science education");
        assertThat(show.getSearchText()).contains("Technology Science Education");
        assertThat(show.getTitleKeyword()).isEqualTo("Complete Show");
        assertThat(show.getIndexedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle show event with minimal fields")
    void shouldHandleShowEventWithMinimalFields() throws InterruptedException {
        // Create minimal show event
        ShowCreatedEvent event = ShowCreatedEvent.builder()
                .showId(UUID.randomUUID())
                .title("Minimal Show")
                .description("Basic description")
                .type("podcast")
                .language("en")
                .isPublic(true)
                .isActive(true)
                .eventId(UUID.randomUUID().toString())
                .eventType("SHOW_CREATED")
                .eventSource("cms-service")
                .eventTimestamp(LocalDateTime.now())
                .build();
        
        // Publish event
        publishShowEvent(event);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify show was indexed with minimal fields
        Optional<ShowDocument> indexedShow = showRepository.findByShowId(event.getShowId());
        
        assertThat(indexedShow).isPresent();
        ShowDocument show = indexedShow.get();
        
        assertThat(show.getTitle()).isEqualTo("Minimal Show");
        assertThat(show.getDescription()).isEqualTo("Basic description");
        assertThat(show.getType()).isEqualTo("podcast");
        assertThat(show.getLanguage()).isEqualTo("en");
        assertThat(show.getIsPublic()).isTrue();
        assertThat(show.getIsActive()).isTrue();
        
        // Verify search text was generated even with minimal fields
        assertThat(show.getSearchText()).isNotNull();
        assertThat(show.getSearchText()).contains("Minimal Show");
        assertThat(show.getSearchText()).contains("Basic description");
    }

    @Test
    @DisplayName("Should handle show events with different types")
    void shouldHandleShowEventsWithDifferentTypes() throws InterruptedException {
        // Create events with different types
        ShowCreatedEvent podcastEvent = createTestShowEvent("Tech Podcast", "Technology podcast", "podcast", "en");
        ShowCreatedEvent documentaryEvent = createTestShowEvent("Science Doc", "Science documentary", "documentary", "en");
        ShowCreatedEvent webinarEvent = createTestShowEvent("Business Webinar", "Business webinar", "webinar", "en");
        
        // Publish events
        publishShowEvent(podcastEvent);
        publishShowEvent(documentaryEvent);
        publishShowEvent(webinarEvent);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify all types were indexed correctly
        assertThat(showRepository.count()).isEqualTo(3);
        
        Optional<ShowDocument> podcast = showRepository.findByShowId(podcastEvent.getShowId());
        Optional<ShowDocument> documentary = showRepository.findByShowId(documentaryEvent.getShowId());
        Optional<ShowDocument> webinar = showRepository.findByShowId(webinarEvent.getShowId());
        
        assertThat(podcast).isPresent();
        assertThat(documentary).isPresent();
        assertThat(webinar).isPresent();
        
        assertThat(podcast.get().getType()).isEqualTo("podcast");
        assertThat(documentary.get().getType()).isEqualTo("documentary");
        assertThat(webinar.get().getType()).isEqualTo("webinar");
    }

    @Test
    @DisplayName("Should handle show events with different languages")
    void shouldHandleShowEventsWithDifferentLanguages() throws InterruptedException {
        // Create events with different languages
        ShowCreatedEvent englishEvent = createTestShowEvent("English Show", "English content", "podcast", "en");
        ShowCreatedEvent spanishEvent = createTestShowEvent("Spanish Show", "Contenido español", "podcast", "es");
        ShowCreatedEvent frenchEvent = createTestShowEvent("French Show", "Contenu français", "podcast", "fr");
        
        // Publish events
        publishShowEvent(englishEvent);
        publishShowEvent(spanishEvent);
        publishShowEvent(frenchEvent);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify all languages were indexed correctly
        assertThat(showRepository.count()).isEqualTo(3);
        
        Optional<ShowDocument> englishShow = showRepository.findByShowId(englishEvent.getShowId());
        Optional<ShowDocument> spanishShow = showRepository.findByShowId(spanishEvent.getShowId());
        Optional<ShowDocument> frenchShow = showRepository.findByShowId(frenchEvent.getShowId());
        
        assertThat(englishShow).isPresent();
        assertThat(spanishShow).isPresent();
        assertThat(frenchShow).isPresent();
        
        assertThat(englishShow.get().getLanguage()).isEqualTo("en");
        assertThat(spanishShow.get().getLanguage()).isEqualTo("es");
        assertThat(frenchShow.get().getLanguage()).isEqualTo("fr");
        
        assertThat(englishShow.get().getDescription()).isEqualTo("English content");
        assertThat(spanishShow.get().getDescription()).isEqualTo("Contenido español");
        assertThat(frenchShow.get().getDescription()).isEqualTo("Contenu français");
    }

    @Test
    @DisplayName("Should handle show events with tags and categories")
    void shouldHandleShowEventsWithTagsAndCategories() throws InterruptedException {
        // Create event with tags and categories
        ShowCreatedEvent event = createTestShowEvent("Tagged Show", "Show with tags and categories");
        event.setTags(List.of("ai", "machine-learning", "technology"));
        event.setCategories(List.of("Technology", "Science", "Education"));
        
        // Publish event
        publishShowEvent(event);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify tags and categories were indexed
        Optional<ShowDocument> indexedShow = showRepository.findByShowId(event.getShowId());
        
        assertThat(indexedShow).isPresent();
        ShowDocument show = indexedShow.get();
        
        assertThat(show.getTags()).containsExactlyInAnyOrder("ai", "machine-learning", "technology");
        assertThat(show.getCategories()).containsExactlyInAnyOrder("Technology", "Science", "Education");
        
        // Verify search text includes tags and categories
        assertThat(show.getSearchText()).contains("ai machine-learning technology");
        assertThat(show.getSearchText()).contains("Technology Science Education");
    }

    @Test
    @DisplayName("Should handle private and inactive shows")
    void shouldHandlePrivateAndInactiveShows() throws InterruptedException {
        // Create private show event
        ShowCreatedEvent privateEvent = createTestShowEvent("Private Show", "Private content");
        privateEvent.setIsPublic(false);
        
        // Create inactive show event
        ShowCreatedEvent inactiveEvent = createTestShowEvent("Inactive Show", "Inactive content");
        inactiveEvent.setIsActive(false);
        
        // Publish events
        publishShowEvent(privateEvent);
        publishShowEvent(inactiveEvent);
        
        // Wait for processing
        waitForMessageProcessing();
        waitForElasticsearchIndexing();

        // Verify shows were indexed but with correct visibility flags
        Optional<ShowDocument> privateShow = showRepository.findByShowId(privateEvent.getShowId());
        Optional<ShowDocument> inactiveShow = showRepository.findByShowId(inactiveEvent.getShowId());
        
        assertThat(privateShow).isPresent();
        assertThat(inactiveShow).isPresent();
        
        assertThat(privateShow.get().getIsPublic()).isFalse();
        assertThat(inactiveShow.get().getIsActive()).isFalse();
        
        // Verify they won't appear in public searches
        assertThat(showRepository.findByIsPublicTrueAndIsActiveTrue(
                org.springframework.data.domain.PageRequest.of(0, 10)).getContent()).isEmpty();
    }
}
