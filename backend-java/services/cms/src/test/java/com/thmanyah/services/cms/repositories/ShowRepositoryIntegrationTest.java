package com.thmanyah.services.cms.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.thmanyah.services.cms.BaseIntegrationTest;
import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/** Integration tests for ShowRepository */
@DisplayName("Show Repository Integration Tests")
public class ShowRepositoryIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("Should save and find show by ID")
  void shouldSaveAndFindShowById() {
    Show show = createTestShow("Test Show", "Test Description");
    Show savedShow = showRepository.save(show);

    Optional<Show> foundShow = showRepository.findById(savedShow.getId());

    assertThat(foundShow).isPresent();
    assertThat(foundShow.get().getTitle()).isEqualTo("Test Show");
    assertThat(foundShow.get().getDescription()).isEqualTo("Test Description");
  }

  @Test
  @DisplayName("Should find shows by title containing")
  void shouldFindShowsByTitleContaining() {
    showRepository.save(createTestShow("Technology Podcast", "About tech"));
    showRepository.save(createTestShow("Science Documentary", "About science"));
    showRepository.save(createTestShow("Tech News", "Latest tech news"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByTitleContainingIgnoreCase("tech", pageable);

    assertThat(results.getContent()).hasSize(2);
    assertThat(results.getContent())
        .extracting(ShowDto::getTitle)
        .containsExactlyInAnyOrder("Technology Podcast", "Tech News");
  }

  @Test
  @DisplayName("Should find shows by type")
  void shouldFindShowsByType() {
    showRepository.save(createTestShow("Podcast 1", "Description", "podcast"));
    showRepository.save(createTestShow("Documentary 1", "Description", "documentary"));
    showRepository.save(createTestShow("Podcast 2", "Description", "podcast"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByType("podcast", pageable);

    assertThat(results.getContent()).hasSize(2);
    assertThat(results.getContent()).extracting(ShowDto::getType).containsOnly("podcast");
  }

  @Test
  @DisplayName("Should find shows by language")
  void shouldFindShowsByLanguage() {
    showRepository.save(createTestShow("Show 1", "Description", "podcast", "en"));
    showRepository.save(createTestShow("Show 2", "Description", "documentary", "es"));
    showRepository.save(createTestShow("Show 3", "Description", "podcast", "en"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByLanguage("en", pageable);

    assertThat(results.getContent()).hasSize(2);
    assertThat(results.getContent()).extracting(ShowDto::getLanguage).containsOnly("en");
  }

  @Test
  @DisplayName("Should find shows by type and language")
  void shouldFindShowsByTypeAndLanguage() {
    showRepository.save(createTestShow("Podcast EN", "Description", "podcast", "en"));
    showRepository.save(createTestShow("Doc EN", "Description", "documentary", "en"));
    showRepository.save(createTestShow("Podcast ES", "Description", "podcast", "es"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByTypeAndLanguage("podcast", "en", pageable);

    assertThat(results.getContent()).hasSize(1);
    assertThat(results.getContent().get(0).getTitle()).isEqualTo("Podcast EN");
  }

  @Test
  @DisplayName("Should find shows by provider")
  void shouldFindShowsByProvider() {
    showRepository.save(createTestShowWithProvider("YouTube Show 1", "youtube"));
    showRepository.save(createTestShowWithProvider("YouTube Show 2", "youtube"));
    showRepository.save(createTestShowWithProvider("Vimeo Show", "vimeo"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByProviderAsDto("youtube", pageable);

    assertThat(results.getContent()).hasSize(2);
    assertThat(results.getContent()).extracting(ShowDto::getProvider).containsOnly("youtube");
  }

  @Test
  @DisplayName("Should find shows created by user")
  void shouldFindShowsCreatedByUser() {
    showRepository.save(createTestShowWithCreator("Show 1", "user1@example.com"));
    showRepository.save(createTestShowWithCreator("Show 2", "user2@example.com"));
    showRepository.save(createTestShowWithCreator("Show 3", "user1@example.com"));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByCreatedBy("user1@example.com", pageable);

    assertThat(results.getContent()).hasSize(2);
    assertThat(results.getContent())
        .extracting(ShowDto::getCreatedBy)
        .containsOnly("user1@example.com");
  }

  @Test
  @DisplayName("Should find shows published between dates")
  void shouldFindShowsPublishedBetweenDates() {
    LocalDate startDate = LocalDate.now().minusDays(10);
    LocalDate endDate = LocalDate.now().minusDays(5);
    LocalDate middleDate = LocalDate.now().minusDays(7);

    showRepository.save(createTestShowWithDate("Old Show", LocalDate.now().minusDays(15)));
    showRepository.save(createTestShowWithDate("Middle Show", middleDate));
    showRepository.save(createTestShowWithDate("Recent Show", LocalDate.now().minusDays(2)));

    Pageable pageable = PageRequest.of(0, 10);
    Page<ShowDto> results = showRepository.findByPublishedAtBetween(startDate, endDate, pageable);

    assertThat(results.getContent()).hasSize(1);
    assertThat(results.getContent().get(0).getTitle()).isEqualTo("Middle Show");
  }

  @Test
  @DisplayName("Should count shows by type")
  void shouldCountShowsByType() {
    showRepository.save(createTestShow("Podcast 1", "Description", "podcast"));
    showRepository.save(createTestShow("Podcast 2", "Description", "podcast"));
    showRepository.save(createTestShow("Documentary 1", "Description", "documentary"));

    long podcastCount = showRepository.countByType("podcast");
    long documentaryCount = showRepository.countByType("documentary");

    assertThat(podcastCount).isEqualTo(2);
    assertThat(documentaryCount).isEqualTo(1);
  }

  @Test
  @DisplayName("Should count shows by language")
  void shouldCountShowsByLanguage() {
    showRepository.save(createTestShow("Show 1", "Description", "podcast", "en"));
    showRepository.save(createTestShow("Show 2", "Description", "podcast", "en"));
    showRepository.save(createTestShow("Show 3", "Description", "documentary", "es"));

    long englishCount = showRepository.countByLanguage("en");
    long spanishCount = showRepository.countByLanguage("es");

    assertThat(englishCount).isEqualTo(2);
    assertThat(spanishCount).isEqualTo(1);
  }

  @Test
  @DisplayName("Should check if title exists")
  void shouldCheckIfTitleExists() {
    showRepository.save(createTestShow("Unique Title", "Description"));

    boolean exists = showRepository.existsByTitleIgnoreCase("Unique Title");
    boolean notExists = showRepository.existsByTitleIgnoreCase("Non-existent Title");

    assertThat(exists).isTrue();
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("Should find distinct types")
  void shouldFindDistinctTypes() {
    showRepository.save(createTestShow("Show 1", "Description", "podcast"));
    showRepository.save(createTestShow("Show 2", "Description", "documentary"));
    showRepository.save(createTestShow("Show 3", "Description", "podcast"));

    List<String> distinctTypes = showRepository.findDistinctTypes();

    assertThat(distinctTypes).containsExactlyInAnyOrder("podcast", "documentary");
  }

  @Test
  @DisplayName("Should find distinct languages")
  void shouldFindDistinctLanguages() {
    showRepository.save(createTestShow("Show 1", "Description", "podcast", "en"));
    showRepository.save(createTestShow("Show 2", "Description", "documentary", "es"));
    showRepository.save(createTestShow("Show 3", "Description", "podcast", "en"));

    List<String> distinctLanguages = showRepository.findDistinctLanguages();

    assertThat(distinctLanguages).containsExactlyInAnyOrder("en", "es");
  }

  private Show createTestShow(String title, String description) {
    return createTestShow(title, description, "podcast", "en");
  }

  private Show createTestShow(String title, String description, String type) {
    return createTestShow(title, description, type, "en");
  }

  private Show createTestShow(String title, String description, String type, String language) {
    return Show.builder()
        .title(title)
        .description(description)
        .type(type)
        .language(language)
        .durationSec(3600)
        .publishedAt(LocalDate.now())
        .provider("test")
        .createdBy("test@example.com")
        .updatedBy("test@example.com")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();
  }

  private Show createTestShowWithProvider(String title, String provider) {
    return Show.builder()
        .title(title)
        .description("Test Description")
        .type("podcast")
        .language("en")
        .durationSec(3600)
        .publishedAt(LocalDate.now())
        .provider(provider)
        .createdBy("test@example.com")
        .updatedBy("test@example.com")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();
  }

  private Show createTestShowWithCreator(String title, String createdBy) {
    return Show.builder()
        .title(title)
        .description("Test Description")
        .type("podcast")
        .language("en")
        .durationSec(3600)
        .publishedAt(LocalDate.now())
        .provider("test")
        .createdBy(createdBy)
        .updatedBy(createdBy)
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();
  }

  private Show createTestShowWithDate(String title, LocalDate publishedAt) {
    return Show.builder()
        .title(title)
        .description("Test Description")
        .type("podcast")
        .language("en")
        .durationSec(3600)
        .publishedAt(publishedAt)
        .provider("test")
        .createdBy("test@example.com")
        .updatedBy("test@example.com")
        .createdDate(LocalDateTime.now())
        .updatedDate(LocalDateTime.now())
        .build();
  }
}
