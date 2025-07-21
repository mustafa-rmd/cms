package com.thmanyah.services.cmsdiscovery.services;

import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchRequest;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchResponse;
import com.thmanyah.services.cmsdiscovery.model.dto.ShowSearchDto;
import com.thmanyah.services.cmsdiscovery.repository.ShowRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final ShowRepository showRepository;

  /** Search shows with advanced filtering and sorting */
  public SearchResponse searchShows(SearchRequest request) {
    long startTime = System.currentTimeMillis();

    log.info("Searching shows with query: '{}', filters: {}", request.getQuery(), request);

    // Create pageable with sorting
    Pageable pageable = createPageable(request);

    Page<ShowDocument> searchResults;

    if (StringUtils.hasText(request.getQuery())) {
      // For now, use simple text search - complex filtering with text search can be enhanced later
      searchResults = showRepository.searchByText(request.getQuery(), pageable);
    } else {
      // Browse all shows with filters
      searchResults = applyFilters(request, pageable);
    }

    // Convert to DTOs
    List<ShowSearchDto> results =
        searchResults.getContent().stream().map(this::convertToDto).toList();

    long executionTime = System.currentTimeMillis() - startTime;

    return SearchResponse.builder()
        .results(results)
        .totalResults(searchResults.getTotalElements())
        .totalPages(searchResults.getTotalPages())
        .currentPage(searchResults.getNumber())
        .pageSize(searchResults.getSize())
        .hasNext(searchResults.hasNext())
        .hasPrevious(searchResults.hasPrevious())
        .executionTimeMs(executionTime)
        .appliedFilters(buildAppliedFilters(request))
        .metadata(buildSearchMetadata(request))
        .build();
  }

  /** Get show by ID */
  public ShowSearchDto getShowById(UUID showId) {
    log.info("Getting show by ID: {}", showId);

    return showRepository.findByShowId(showId).map(this::convertToDto).orElse(null);
  }

  /** Get popular shows */
  public SearchResponse getPopularShows(int page, int size) {
    log.info("Getting popular shows - page: {}, size: {}", page, size);

    Pageable pageable = PageRequest.of(page, size);
    Page<ShowDocument> results =
        showRepository.findByIsPublicTrueAndIsActiveTrueOrderByViewCountDesc(pageable);

    return buildSimpleResponse(results, "popular");
  }

  /** Get recent shows */
  public SearchResponse getRecentShows(int page, int size) {
    log.info("Getting recent shows - page: {}, size: {}", page, size);

    Pageable pageable = PageRequest.of(page, size);
    Page<ShowDocument> results =
        showRepository.findByIsPublicTrueAndIsActiveTrueOrderByCreatedDateDesc(pageable);

    return buildSimpleResponse(results, "recent");
  }

  /** Get shows by type */
  public SearchResponse getShowsByType(String type, int page, int size) {
    log.info("Getting shows by type: {} - page: {}, size: {}", type, page, size);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<ShowDocument> results =
        showRepository.findByTypeAndIsPublicTrueAndIsActiveTrue(type, pageable);

    return buildSimpleResponse(results, "type:" + type);
  }

  /** Get shows by tag */
  public SearchResponse getShowsByTag(String tag, int page, int size) {
    log.info("Getting shows by tag: {} - page: {}, size: {}", tag, page, size);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<ShowDocument> results =
        showRepository.findByTagsContainingAndIsPublicTrueAndIsActiveTrue(tag, pageable);

    return buildSimpleResponse(results, "tag:" + tag);
  }

  /** Get shows by category */
  public SearchResponse getShowsByCategory(String category, int page, int size) {
    log.info("Getting shows by category: {} - page: {}, size: {}", category, page, size);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
    Page<ShowDocument> results =
        showRepository.findByCategoriesContainingAndIsPublicTrueAndIsActiveTrue(category, pageable);

    return buildSimpleResponse(results, "category:" + category);
  }

  private Pageable createPageable(SearchRequest request) {
    Sort sort = createSort(request.getSortBy(), request.getSortDirection());
    return PageRequest.of(request.getPage(), request.getSize(), sort);
  }

  private Sort createSort(String sortBy, String sortDirection) {
    Sort.Direction direction =
        "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

    return switch (sortBy) {
      case "createdDate" -> Sort.by(direction, "createdDate");
      case "publishedAt" -> Sort.by(direction, "publishedAt");
      case "viewCount" -> Sort.by(direction, "viewCount");
      case "rating" -> Sort.by(direction, "rating");
      case "title" -> Sort.by(direction, "titleKeyword");
      default -> Sort.by(Sort.Direction.DESC, "_score"); // Relevance
    };
  }

  private Page<ShowDocument> applyFilters(SearchRequest request, Pageable pageable) {
    // Apply filters using repository methods based on available criteria

    // Check for type filter
    if (StringUtils.hasText(request.getType())) {
      return applyTypeBasedFilters(request, pageable);
    }

    // Check for language filter
    if (StringUtils.hasText(request.getLanguage())) {
      return applyLanguageBasedFilters(request, pageable);
    }

    // Check for provider filter
    if (StringUtils.hasText(request.getProvider())) {
      return applyProviderBasedFilters(request, pageable);
    }

    // Check for duration filter
    if (request.getMinDuration() != null || request.getMaxDuration() != null) {
      return applyDurationBasedFilters(request, pageable);
    }

    // Check for date range filter
    if (request.getPublishedAfter() != null || request.getPublishedBefore() != null) {
      return applyDateBasedFilters(request, pageable);
    }

    // Check for rating filter
    if (request.getMinRating() != null) {
      return showRepository.findByRatingGreaterThanEqualAndIsPublicTrueAndIsActiveTrue(
          request.getMinRating(), pageable);
    }

    // Check for tags filter
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      // For now, use the first tag - in a real implementation, you'd want to handle multiple tags
      return showRepository.findByTagsContainingAndIsPublicTrueAndIsActiveTrue(
          request.getTags().get(0), pageable);
    }

    // Check for categories filter
    if (request.getCategories() != null && !request.getCategories().isEmpty()) {
      // For now, use the first category - in a real implementation, you'd want to handle multiple categories
      return showRepository.findByCategoriesContainingAndIsPublicTrueAndIsActiveTrue(
          request.getCategories().get(0), pageable);
    }

    // No filters applied, return all public and active shows
    return showRepository.findByIsPublicTrueAndIsActiveTrue(pageable);
  }

  private Page<ShowDocument> applyTypeBasedFilters(SearchRequest request, Pageable pageable) {
    String type = request.getType();

    // Type + Language + Duration
    if (StringUtils.hasText(request.getLanguage()) &&
        (request.getMinDuration() != null || request.getMaxDuration() != null)) {
      Integer minDur = request.getMinDuration() != null ? request.getMinDuration() : 0;
      Integer maxDur = request.getMaxDuration() != null ? request.getMaxDuration() : Integer.MAX_VALUE;
      return showRepository.findByTypeAndLanguageAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
          type, request.getLanguage(), minDur, maxDur, pageable);
    }

    // Type + Language
    if (StringUtils.hasText(request.getLanguage())) {
      return showRepository.findByTypeAndLanguageAndIsPublicTrueAndIsActiveTrue(
          type, request.getLanguage(), pageable);
    }

    // Type + Duration
    if (request.getMinDuration() != null || request.getMaxDuration() != null) {
      Integer minDur = request.getMinDuration() != null ? request.getMinDuration() : 0;
      Integer maxDur = request.getMaxDuration() != null ? request.getMaxDuration() : Integer.MAX_VALUE;
      return showRepository.findByTypeAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
          type, minDur, maxDur, pageable);
    }

    // Type only
    return showRepository.findByTypeAndIsPublicTrueAndIsActiveTrue(type, pageable);
  }

  private Page<ShowDocument> applyLanguageBasedFilters(SearchRequest request, Pageable pageable) {
    String language = request.getLanguage();

    // Language + Duration
    if (request.getMinDuration() != null || request.getMaxDuration() != null) {
      Integer minDur = request.getMinDuration() != null ? request.getMinDuration() : 0;
      Integer maxDur = request.getMaxDuration() != null ? request.getMaxDuration() : Integer.MAX_VALUE;
      return showRepository.findByLanguageAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
          language, minDur, maxDur, pageable);
    }

    // Language only
    return showRepository.findByLanguageAndIsPublicTrueAndIsActiveTrue(language, pageable);
  }

  private Page<ShowDocument> applyProviderBasedFilters(SearchRequest request, Pageable pageable) {
    // Provider only for now - can be extended for combinations
    return showRepository.findByProviderAndIsPublicTrueAndIsActiveTrue(request.getProvider(), pageable);
  }

  private Page<ShowDocument> applyDurationBasedFilters(SearchRequest request, Pageable pageable) {
    Integer minDur = request.getMinDuration() != null ? request.getMinDuration() : 0;
    Integer maxDur = request.getMaxDuration() != null ? request.getMaxDuration() : Integer.MAX_VALUE;
    return showRepository.findByDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(minDur, maxDur, pageable);
  }

  private Page<ShowDocument> applyDateBasedFilters(SearchRequest request, Pageable pageable) {
    LocalDate startDate = request.getPublishedAfter() != null ? request.getPublishedAfter() : LocalDate.of(1900, 1, 1);
    LocalDate endDate = request.getPublishedBefore() != null ? request.getPublishedBefore() : LocalDate.now();
    return showRepository.findByPublishedAtBetweenAndIsPublicTrueAndIsActiveTrue(startDate, endDate, pageable);
  }

  private ShowSearchDto convertToDto(ShowDocument document) {
    return ShowSearchDto.builder()
        .showId(document.getShowId())
        .title(document.getTitle())
        .description(document.getDescription())
        .type(document.getType())
        .language(document.getLanguage())
        .durationSec(document.getDurationSec())
        .publishedAt(document.getPublishedAt())
        .provider(document.getProvider())
        .externalId(document.getExternalId())
        .tags(document.getTags())
        .categories(document.getCategories())
        .thumbnailUrl(document.getThumbnailUrl())
        .streamUrl(document.getStreamUrl())
        .viewCount(document.getViewCount())
        .rating(document.getRating())
        .createdDate(document.getCreatedDate())
        .updatedDate(document.getUpdatedDate())
        .createdBy(document.getCreatedBy())
        .updatedBy(document.getUpdatedBy())
        .build();
  }

  private Map<String, Object> buildAppliedFilters(SearchRequest request) {
    Map<String, Object> filters = new HashMap<>();

    if (StringUtils.hasText(request.getType())) {
      filters.put("type", request.getType());
    }
    if (StringUtils.hasText(request.getLanguage())) {
      filters.put("language", request.getLanguage());
    }
    if (StringUtils.hasText(request.getProvider())) {
      filters.put("provider", request.getProvider());
    }
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      filters.put("tags", request.getTags());
    }
    if (request.getCategories() != null && !request.getCategories().isEmpty()) {
      filters.put("categories", request.getCategories());
    }
    if (request.getMinDuration() != null) {
      filters.put("minDuration", request.getMinDuration());
    }
    if (request.getMaxDuration() != null) {
      filters.put("maxDuration", request.getMaxDuration());
    }
    if (request.getPublishedAfter() != null) {
      filters.put("publishedAfter", request.getPublishedAfter());
    }
    if (request.getPublishedBefore() != null) {
      filters.put("publishedBefore", request.getPublishedBefore());
    }
    if (request.getMinRating() != null) {
      filters.put("minRating", request.getMinRating());
    }

    return filters;
  }

  private SearchResponse.SearchMetadata buildSearchMetadata(SearchRequest request) {
    return SearchResponse.SearchMetadata.builder()
        .originalQuery(request.getQuery())
        .processedQuery(request.getQuery())
        .searchType(StringUtils.hasText(request.getQuery()) ? "full_text" : "browse")
        .fuzzyApplied(Boolean.TRUE.equals(request.getFuzzy()))
        .highlightApplied(Boolean.TRUE.equals(request.getHighlight()))
        .build();
  }

  private SearchResponse buildSimpleResponse(Page<ShowDocument> results, String searchType) {
    List<ShowSearchDto> dtos = results.getContent().stream().map(this::convertToDto).toList();

    return SearchResponse.builder()
        .results(dtos)
        .totalResults(results.getTotalElements())
        .totalPages(results.getTotalPages())
        .currentPage(results.getNumber())
        .pageSize(results.getSize())
        .hasNext(results.hasNext())
        .hasPrevious(results.hasPrevious())
        .executionTimeMs(0L)
        .appliedFilters(Map.of("searchType", searchType))
        .build();
  }

}
