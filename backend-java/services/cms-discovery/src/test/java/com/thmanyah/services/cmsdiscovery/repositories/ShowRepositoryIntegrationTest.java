package com.thmanyah.services.cmsdiscovery.repositories;

import com.thmanyah.services.cmsdiscovery.BaseIntegrationTest;
import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ShowRepository with Elasticsearch
 */
@DisplayName("Show Repository Integration Tests")
public class ShowRepositoryIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should save and find show by ID")
    void shouldSaveAndFindShowById() throws InterruptedException {
        ShowDocument show = createTestShowDocument("Test Show", "Test Description");
        
        waitForElasticsearchIndexing();

        Optional<ShowDocument> foundShow = showRepository.findById(show.getId());

        assertThat(foundShow).isPresent();
        assertThat(foundShow.get().getTitle()).isEqualTo("Test Show");
        assertThat(foundShow.get().getDescription()).isEqualTo("Test Description");
    }

    @Test
    @DisplayName("Should find show by show ID")
    void shouldFindShowByShowId() throws InterruptedException {
        ShowDocument show = createTestShowDocument("Test Show", "Test Description");
        
        waitForElasticsearchIndexing();

        Optional<ShowDocument> foundShow = showRepository.findByShowId(show.getShowId());

        assertThat(foundShow).isPresent();
        assertThat(foundShow.get().getShowId()).isEqualTo(show.getShowId());
        assertThat(foundShow.get().getTitle()).isEqualTo("Test Show");
    }

    @Test
    @DisplayName("Should find public and active shows")
    void shouldFindPublicAndActiveShows() throws InterruptedException {
        // Create public and active show
        ShowDocument publicShow = createTestShowDocument("Public Show", "Public content");
        
        // Create private show
        ShowDocument privateShow = createTestShowDocument("Private Show", "Private content");
        privateShow.setIsPublic(false);
        showRepository.save(privateShow);
        
        // Create inactive show
        ShowDocument inactiveShow = createTestShowDocument("Inactive Show", "Inactive content");
        inactiveShow.setIsActive(false);
        showRepository.save(inactiveShow);
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> results = showRepository.findByIsPublicTrueAndIsActiveTrue(pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("Public Show");
        assertThat(results.getContent().get(0).getIsPublic()).isTrue();
        assertThat(results.getContent().get(0).getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should find shows by type")
    void shouldFindShowsByType() throws InterruptedException {
        // Create shows with different types
        createTestShowDocument("Podcast 1", "First podcast", "podcast", "en");
        createTestShowDocument("Podcast 2", "Second podcast", "podcast", "en");
        createTestShowDocument("Documentary 1", "First documentary", "documentary", "en");
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> podcastResults = showRepository.findByTypeAndIsPublicTrueAndIsActiveTrue("podcast", pageable);
        Page<ShowDocument> documentaryResults = showRepository.findByTypeAndIsPublicTrueAndIsActiveTrue("documentary", pageable);

        assertThat(podcastResults.getContent()).hasSize(2);
        assertThat(documentaryResults.getContent()).hasSize(1);
        
        assertThat(podcastResults.getContent())
                .extracting(ShowDocument::getType)
                .containsOnly("podcast");
        
        assertThat(documentaryResults.getContent())
                .extracting(ShowDocument::getType)
                .containsOnly("documentary");
    }

    @Test
    @DisplayName("Should find shows by language")
    void shouldFindShowsByLanguage() throws InterruptedException {
        // Create shows with different languages
        createTestShowDocument("English Show 1", "English content 1", "podcast", "en");
        createTestShowDocument("English Show 2", "English content 2", "documentary", "en");
        createTestShowDocument("Spanish Show", "Contenido español", "podcast", "es");
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> englishResults = showRepository.findByLanguageAndIsPublicTrueAndIsActiveTrue("en", pageable);
        Page<ShowDocument> spanishResults = showRepository.findByLanguageAndIsPublicTrueAndIsActiveTrue("es", pageable);

        assertThat(englishResults.getContent()).hasSize(2);
        assertThat(spanishResults.getContent()).hasSize(1);
        
        assertThat(englishResults.getContent())
                .extracting(ShowDocument::getLanguage)
                .containsOnly("en");
        
        assertThat(spanishResults.getContent())
                .extracting(ShowDocument::getLanguage)
                .containsOnly("es");
    }

    @Test
    @DisplayName("Should find shows by provider")
    void shouldFindShowsByProvider() throws InterruptedException {
        // Create shows with different providers
        ShowDocument youtubeShow = createTestShowDocument("YouTube Show", "YouTube content");
        youtubeShow.setProvider("youtube");
        showRepository.save(youtubeShow);
        
        ShowDocument vimeoShow = createTestShowDocument("Vimeo Show", "Vimeo content");
        vimeoShow.setProvider("vimeo");
        showRepository.save(vimeoShow);
        
        ShowDocument internalShow = createTestShowDocument("Internal Show", "Internal content");
        internalShow.setProvider("internal");
        showRepository.save(internalShow);
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> youtubeResults = showRepository.findByProviderAndIsPublicTrueAndIsActiveTrue("youtube", pageable);
        Page<ShowDocument> vimeoResults = showRepository.findByProviderAndIsPublicTrueAndIsActiveTrue("vimeo", pageable);

        assertThat(youtubeResults.getContent()).hasSize(1);
        assertThat(vimeoResults.getContent()).hasSize(1);
        
        assertThat(youtubeResults.getContent().get(0).getProvider()).isEqualTo("youtube");
        assertThat(vimeoResults.getContent().get(0).getProvider()).isEqualTo("vimeo");
    }

    @Test
    @DisplayName("Should find shows published after date")
    void shouldFindShowsPublishedAfterDate() throws InterruptedException {
        LocalDate cutoffDate = LocalDate.now().minusDays(7);
        
        // Create old show
        ShowDocument oldShow = createTestShowDocument("Old Show", "Old content");
        oldShow.setPublishedAt(LocalDate.now().minusDays(10));
        showRepository.save(oldShow);
        
        // Create recent show
        ShowDocument recentShow = createTestShowDocument("Recent Show", "Recent content");
        recentShow.setPublishedAt(LocalDate.now().minusDays(3));
        showRepository.save(recentShow);
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> results = showRepository.findByPublishedAtAfterAndIsPublicTrueAndIsActiveTrue(cutoffDate, pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("Recent Show");
        assertThat(results.getContent().get(0).getPublishedAt()).isAfter(cutoffDate);
    }

    @Test
    @DisplayName("Should find shows by duration range")
    void shouldFindShowsByDurationRange() throws InterruptedException {
        // Create shows with different durations
        ShowDocument shortShow = createTestShowDocument("Short Show", "Short content");
        shortShow.setDurationSec(1800); // 30 minutes
        showRepository.save(shortShow);
        
        ShowDocument mediumShow = createTestShowDocument("Medium Show", "Medium content");
        mediumShow.setDurationSec(3600); // 1 hour
        showRepository.save(mediumShow);
        
        ShowDocument longShow = createTestShowDocument("Long Show", "Long content");
        longShow.setDurationSec(7200); // 2 hours
        showRepository.save(longShow);
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> results = showRepository.findByDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(3000, 4000, pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).isEqualTo("Medium Show");
        assertThat(results.getContent().get(0).getDurationSec()).isBetween(3000, 4000);
    }

    @Test
    @DisplayName("Should perform full-text search")
    void shouldPerformFullTextSearch() throws InterruptedException {
        // Create shows with searchable content
        createTestShowDocument("Technology Podcast", "Latest tech trends and innovations in AI");
        createTestShowDocument("Science Documentary", "Exploring the wonders of modern science");
        createTestShowDocument("Tech News", "Daily technology news and updates");
        
        waitForElasticsearchIndexing();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShowDocument> techResults = showRepository.searchByText("technology", pageable);
        Page<ShowDocument> scienceResults = showRepository.searchByText("science", pageable);

        assertThat(techResults.getContent()).hasSizeGreaterThan(0);
        assertThat(scienceResults.getContent()).hasSize(1);
        
        // Verify search results contain the search term
        assertThat(techResults.getContent())
                .extracting(ShowDocument::getTitle)
                .anyMatch(title -> title.toLowerCase().contains("tech"));
    }

    @Test
    @DisplayName("Should handle search text generation")
    void shouldHandleSearchTextGeneration() throws InterruptedException {
        ShowDocument show = ShowDocument.builder()
                .showId(UUID.randomUUID())
                .title("AI Technology")
                .description("Artificial Intelligence and Machine Learning")
                .type("podcast")
                .language("en")
                .tags(List.of("ai", "technology", "ml"))
                .categories(List.of("Technology", "Science"))
                .isPublic(true)
                .isActive(true)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Generate search text
        show.generateSearchText();
        
        ShowDocument savedShow = showRepository.save(show);
        waitForElasticsearchIndexing();

        assertThat(savedShow.getSearchText()).isNotNull();
        assertThat(savedShow.getSearchText()).contains("AI Technology");
        assertThat(savedShow.getSearchText()).contains("Artificial Intelligence");
        assertThat(savedShow.getSearchText()).contains("ai technology ml");
        assertThat(savedShow.getSearchText()).contains("Technology Science");
        assertThat(savedShow.getTitleKeyword()).isEqualTo("AI Technology");
        assertThat(savedShow.getIndexedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should count documents correctly")
    void shouldCountDocumentsCorrectly() throws InterruptedException {
        // Initially no documents
        assertThat(showRepository.count()).isEqualTo(0);
        
        // Create test shows
        createTestShowDocument("Show 1", "Description 1");
        createTestShowDocument("Show 2", "Description 2");
        createTestShowDocument("Show 3", "Description 3");
        
        waitForElasticsearchIndexing();

        assertThat(showRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should delete documents correctly")
    void shouldDeleteDocumentsCorrectly() throws InterruptedException {
        // Create test shows
        ShowDocument show1 = createTestShowDocument("Show 1", "Description 1");
        ShowDocument show2 = createTestShowDocument("Show 2", "Description 2");
        
        waitForElasticsearchIndexing();
        assertThat(showRepository.count()).isEqualTo(2);

        // Delete one show
        showRepository.delete(show1);
        waitForElasticsearchIndexing();
        
        assertThat(showRepository.count()).isEqualTo(1);
        assertThat(showRepository.findById(show1.getId())).isEmpty();
        assertThat(showRepository.findById(show2.getId())).isPresent();
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() throws InterruptedException {
        // Create multiple test shows
        for (int i = 1; i <= 7; i++) {
            createTestShowDocument("Show " + i, "Description " + i);
        }
        
        waitForElasticsearchIndexing();

        // Test first page
        Pageable firstPage = PageRequest.of(0, 3);
        Page<ShowDocument> firstResults = showRepository.findByIsPublicTrueAndIsActiveTrue(firstPage);
        
        assertThat(firstResults.getContent()).hasSize(3);
        assertThat(firstResults.getTotalElements()).isEqualTo(7);
        assertThat(firstResults.getTotalPages()).isEqualTo(3);
        assertThat(firstResults.getNumber()).isEqualTo(0);
        assertThat(firstResults.hasNext()).isTrue();
        assertThat(firstResults.hasPrevious()).isFalse();

        // Test second page
        Pageable secondPage = PageRequest.of(1, 3);
        Page<ShowDocument> secondResults = showRepository.findByIsPublicTrueAndIsActiveTrue(secondPage);
        
        assertThat(secondResults.getContent()).hasSize(3);
        assertThat(secondResults.getNumber()).isEqualTo(1);
        assertThat(secondResults.hasNext()).isTrue();
        assertThat(secondResults.hasPrevious()).isTrue();

        // Test last page
        Pageable lastPage = PageRequest.of(2, 3);
        Page<ShowDocument> lastResults = showRepository.findByIsPublicTrueAndIsActiveTrue(lastPage);
        
        assertThat(lastResults.getContent()).hasSize(1);
        assertThat(lastResults.getNumber()).isEqualTo(2);
        assertThat(lastResults.hasNext()).isFalse();
        assertThat(lastResults.hasPrevious()).isTrue();
    }
}
