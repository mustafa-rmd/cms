package com.thmanyah.services.cmsdiscovery.services;

import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchRequest;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchResponse;
import com.thmanyah.services.cmsdiscovery.model.dto.ShowSearchDto;
import com.thmanyah.services.cmsdiscovery.repository.ShowRepository;
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
      // Full-text search
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
    // For now, return all public and active shows
    // TODO: Implement complex filtering with Elasticsearch queries
    return showRepository.findByIsPublicTrueAndIsActiveTrue(pageable);
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
