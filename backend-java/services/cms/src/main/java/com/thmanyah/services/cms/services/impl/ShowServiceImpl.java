package com.thmanyah.services.cms.services.impl;

import com.thmanyah.services.cms.exception.ShowAlreadyExistsException;
import com.thmanyah.services.cms.exception.ShowNotFoundException;
import com.thmanyah.services.cms.mapper.ShowMapper;
import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import com.thmanyah.services.cms.model.dto.ShowDto;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import com.thmanyah.services.cms.repositories.ShowRepository;
import com.thmanyah.services.cms.services.EventPublisherService;
import com.thmanyah.services.cms.services.ShowService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

  private final ShowRepository showRepository;
  private final ShowMapper showMapper;
  private final EventPublisherService eventPublisherService;

  @Override
  @Transactional
  public ShowDto createShow(String userEmail, ShowCreateUpdateDto showCreateUpdateDto) {
    log.info("Creating show with title: {} by user: {}", showCreateUpdateDto.getTitle(), userEmail);

    // Check if title already exists
    if (showRepository.existsByTitleIgnoreCase(showCreateUpdateDto.getTitle())) {
      throw new ShowAlreadyExistsException(showCreateUpdateDto.getTitle());
    }

    Show show = showMapper.toEntity(userEmail, showCreateUpdateDto);
    Show savedShow = showRepository.save(show);

    log.info("Successfully created show with ID: {}", savedShow.getId());

    // Publish show created event
    eventPublisherService.publishShowCreatedEvent(savedShow);

    return showRepository
        .findShowDtoById(savedShow.getId())
        .orElseThrow(() -> new ShowNotFoundException(savedShow.getId()));
  }

  @Override
  @Transactional
  public ShowDto updateShow(String userEmail, UUID id, ShowCreateUpdateDto showCreateUpdateDto) {
    log.info("Updating show with ID: {} by user: {}", id, userEmail);

    Show existingShow = getShowById(id);

    // Check if title already exists for a different show
    if (!existingShow.getTitle().equalsIgnoreCase(showCreateUpdateDto.getTitle())
        && showRepository.existsByTitleIgnoreCase(showCreateUpdateDto.getTitle())) {
      throw new ShowAlreadyExistsException(showCreateUpdateDto.getTitle());
    }

    showMapper.updateEntity(userEmail, showCreateUpdateDto, existingShow);
    Show savedShow = showRepository.save(existingShow);

    log.info("Successfully updated show with ID: {}", savedShow.getId());

    // Publish show updated event
    eventPublisherService.publishShowUpdatedEvent(savedShow);

    return showRepository
        .findShowDtoById(savedShow.getId())
        .orElseThrow(() -> new ShowNotFoundException(savedShow.getId()));
  }

  @Override
  @Transactional
  public Page<ShowDto> getAllShows(int pageNumber, int pageSize) {
    log.debug("Getting all shows - page: {}, size: {}", pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findAllShowsAsDto(pageable);
  }

  @Override
  @Transactional
  public Page<ShowDto> getShowsByProvider(String provider, int pageNumber, int pageSize) {
    log.debug(
        "Getting shows by provider '{}' - page: {}, size: {}", provider, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByProviderAsDto(provider, pageable);
  }

  @Override
  @Transactional
  public ShowDto getShowDtoById(UUID id) {
    log.debug("Getting show DTO by ID: {}", id);
    return showRepository.findShowDtoById(id).orElseThrow(() -> new ShowNotFoundException(id));
  }

  @Override
  @Transactional
  public Show getShowById(UUID id) {
    log.debug("Getting show entity by ID: {}", id);
    return showRepository.findById(id).orElseThrow(() -> new ShowNotFoundException(id));
  }

  @Override
  @Transactional
  public void deleteShow(String userEmail, UUID id) {
    log.info("Deleting show with ID: {} by user: {}", id, userEmail);

    Show show = getShowById(id);

    // Publish show deleted event before deletion
    eventPublisherService.publishShowDeletedEvent(show);

    showRepository.delete(show);

    log.info("Successfully deleted show with ID: {}", id);
  }

  @Override
  @Transactional
  public void deleteShows(String userEmail, Set<UUID> ids) {
    log.info("Deleting {} shows by user: {}", ids.size(), userEmail);

    List<Show> shows = showRepository.findByIdIn(new ArrayList<>(ids));
    if (shows.size() != ids.size()) {
      throw new ShowNotFoundException("Some shows were not found for deletion");
    }

    // Publish show deleted events before deletion
    shows.forEach(eventPublisherService::publishShowDeletedEvent);

    showRepository.deleteAll(shows);
    log.info("Successfully deleted {} shows", shows.size());
  }

  @Override
  @Transactional
  public Page<ShowDto> getShowsByType(String type, int pageNumber, int pageSize) {
    log.debug("Getting shows by type: {} - page: {}, size: {}", type, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByType(type, pageable);
  }

  @Override
  @Transactional
  public Page<ShowDto> getShowsByLanguage(String language, int pageNumber, int pageSize) {
    log.debug("Getting shows by language: {} - page: {}, size: {}", language, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByLanguage(language, pageable);
  }

  @Override
  @Transactional
  public Page<ShowDto> getShowsByPublishedAt(LocalDate publishedAt, int pageNumber, int pageSize) {
    log.debug(
        "Getting shows by published date: {} - page: {}, size: {}",
        publishedAt,
        pageNumber,
        pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByPublishedAt(publishedAt, pageable);
  }

  @Override
  @Transactional
  public Page<ShowDto> searchShowsByTitle(String title, int pageNumber, int pageSize) {
    log.debug("Searching shows by title: {} - page: {}, size: {}", title, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByTitleContainingIgnoreCase(title, pageable);
  }

  @Override
  @Transactional
  public Page<ShowDto> getShowsByCreatedBy(String userEmail, int pageNumber, int pageSize) {
    log.debug("Getting shows created by: {} - page: {}, size: {}", userEmail, pageNumber, pageSize);
    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
    return showRepository.findByCreatedBy(userEmail, pageable);
  }

  @Override
  @Transactional
  public Map<String, Map<String, Object>> getDistinctFields() {
    log.debug("Getting distinct fields for filtering");

    Map<String, Map<String, Object>> distinctFields = new HashMap<>();

    // Types
    List<String> types = showRepository.findDistinctTypes();
    Map<String, Object> typeMap = new HashMap<>();
    types.forEach(type -> typeMap.put(type, showRepository.countByType(type)));
    distinctFields.put("type", typeMap);

    // Languages
    List<String> languages = showRepository.findDistinctLanguages();
    Map<String, Object> languageMap = new HashMap<>();
    languages.forEach(
        language -> languageMap.put(language, showRepository.countByLanguage(language)));
    distinctFields.put("language", languageMap);

    return distinctFields;
  }

  @Override
  @Transactional
  public Map<String, Object> getShowStatistics() {
    log.debug("Getting show statistics");

    Map<String, Object> statistics = new HashMap<>();
    statistics.put("totalShows", showRepository.count());

    // Count by type
    Map<String, Long> typeStats = new HashMap<>();
    showRepository
        .findDistinctTypes()
        .forEach(type -> typeStats.put(type, showRepository.countByType(type)));
    statistics.put("showsByType", typeStats);

    // Count by language
    Map<String, Long> languageStats = new HashMap<>();
    showRepository
        .findDistinctLanguages()
        .forEach(language -> languageStats.put(language, showRepository.countByLanguage(language)));
    statistics.put("showsByLanguage", languageStats);

    return statistics;
  }

  @Override
  @Transactional
  public Show saveFromImport(
      String userEmail, ShowExternalDto externalDto, boolean skipDuplicates) {
    log.info(
        "Importing show '{}' from external source by user: {}", externalDto.title(), userEmail);

    // Check for duplicates if requested
    if (skipDuplicates && showRepository.existsByTitleIgnoreCase(externalDto.title())) {
      log.debug("Skipping duplicate show: {}", externalDto.title());
      return null;
    }

    // Create show entity from external DTO
    Show show =
        Show.builder()
            .type(externalDto.type())
            .title(externalDto.title())
            .description(externalDto.description())
            .language(externalDto.language())
            .durationSec(externalDto.durationSec())
            .publishedAt(externalDto.publishedAt())
            .provider(
                externalDto.sourceProvider() != null ? externalDto.sourceProvider() : "external")
            .createdBy(userEmail)
            .updatedBy(userEmail)
            .build();

    Show savedShow = showRepository.save(show);
    log.info("Successfully imported show with ID: {}", savedShow.getId());

    return savedShow;
  }

  @Override
  @Transactional
  public List<Show> saveAllFromImport(
      String userEmail, List<ShowExternalDto> externalDtos, boolean skipDuplicates) {
    log.info("Starting batch import of {} shows for user: {}", externalDtos.size(), userEmail);

    List<Show> showsToSave = new ArrayList<>();

    for (ShowExternalDto externalDto : externalDtos) {
      // Check for duplicates if required
      if (skipDuplicates && existsByTitle(externalDto.title())) {
        log.debug("Skipping duplicate show: {}", externalDto.title());
        continue;
      }

      // Convert external DTO to Show entity (let JPA generate the ID)
      Show show =
          Show.builder()
              .type(externalDto.type())
              .title(externalDto.title())
              .description(externalDto.description())
              .language(externalDto.language())
              .durationSec(externalDto.durationSec())
              .publishedAt(externalDto.publishedAt())
              .provider(
                  externalDto.sourceProvider() != null ? externalDto.sourceProvider() : "external")
              .createdDate(LocalDateTime.now())
              .updatedDate(LocalDateTime.now())
              .createdBy(userEmail)
              .updatedBy(userEmail)
              .build();

      showsToSave.add(show);
    }

    // Batch save all shows
    List<Show> savedShows = showRepository.saveAll(showsToSave);
    showRepository.flush(); // Force immediate persistence
    log.info("Successfully batch imported {} shows", savedShows.size());

    return savedShows;
  }

  @Override
  @Transactional
  public boolean existsByTitle(String title) {
    return showRepository.existsByTitleIgnoreCase(title);
  }
}
