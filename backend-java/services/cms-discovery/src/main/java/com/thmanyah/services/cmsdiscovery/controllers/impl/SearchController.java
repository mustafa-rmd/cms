package com.thmanyah.services.cmsdiscovery.controllers.impl;

import com.thmanyah.services.cmsdiscovery.controllers.SearchControllerV1;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchRequest;
import com.thmanyah.services.cmsdiscovery.model.dto.SearchResponse;
import com.thmanyah.services.cmsdiscovery.model.dto.ShowSearchDto;
import com.thmanyah.services.cmsdiscovery.services.SearchService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class SearchController implements SearchControllerV1 {

  private final SearchService searchService;

  @Override
  @GetMapping("/search")
  public ResponseEntity<SearchResponse> searchShows(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String language,
      @RequestParam(required = false) String provider,
      @RequestParam(required = false) String tags,
      @RequestParam(required = false) String categories,
      @RequestParam(required = false) Integer minDuration,
      @RequestParam(required = false) Integer maxDuration,
      @RequestParam(required = false) String publishedAfter,
      @RequestParam(required = false) String publishedBefore,
      @RequestParam(required = false) Double minRating,
      @RequestParam(defaultValue = "relevance") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "true") Boolean highlight,
      @RequestParam(defaultValue = "true") Boolean fuzzy) {

    log.info("Search request - query: '{}', type: {}, page: {}, size: {}", query, type, page, size);

    SearchRequest searchRequest =
        SearchRequest.builder()
            .query(query)
            .type(type)
            .language(language)
            .provider(provider)
            .tags(parseCommaSeparatedList(tags))
            .categories(parseCommaSeparatedList(categories))
            .minDuration(minDuration)
            .maxDuration(maxDuration)
            .publishedAfter(parseDate(publishedAfter))
            .publishedBefore(parseDate(publishedBefore))
            .minRating(minRating)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .page(page)
            .size(size)
            .highlight(highlight)
            .fuzzy(fuzzy)
            .build();

    SearchResponse response = searchService.searchShows(searchRequest);
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping("/search")
  public ResponseEntity<SearchResponse> advancedSearch(
      @Valid @RequestBody SearchRequest searchRequest) {
    log.info("Advanced search request: {}", searchRequest);

    SearchResponse response = searchService.searchShows(searchRequest);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/shows/{showId}")
  public ResponseEntity<ShowSearchDto> getShowById(@PathVariable UUID showId) {
    log.info("Getting show by ID: {}", showId);

    ShowSearchDto show = searchService.getShowById(showId);
    if (show == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(show);
  }

  @Override
  @GetMapping("/shows/popular")
  public ResponseEntity<SearchResponse> getPopularShows(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    log.info("Getting popular shows - page: {}, size: {}", page, size);

    SearchResponse response = searchService.getPopularShows(page, size);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/shows/recent")
  public ResponseEntity<SearchResponse> getRecentShows(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    log.info("Getting recent shows - page: {}, size: {}", page, size);

    SearchResponse response = searchService.getRecentShows(page, size);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/shows/type/{type}")
  public ResponseEntity<SearchResponse> getShowsByType(
      @PathVariable String type,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    log.info("Getting shows by type: {} - page: {}, size: {}", type, page, size);

    SearchResponse response = searchService.getShowsByType(type, page, size);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/shows/tag/{tag}")
  public ResponseEntity<SearchResponse> getShowsByTag(
      @PathVariable String tag,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    log.info("Getting shows by tag: {} - page: {}, size: {}", tag, page, size);

    SearchResponse response = searchService.getShowsByTag(tag, page, size);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping("/shows/category/{category}")
  public ResponseEntity<SearchResponse> getShowsByCategory(
      @PathVariable String category,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {

    log.info("Getting shows by category: {} - page: {}, size: {}", category, page, size);

    SearchResponse response = searchService.getShowsByCategory(category, page, size);
    return ResponseEntity.ok(response);
  }

  private List<String> parseCommaSeparatedList(String value) {
    if (!StringUtils.hasText(value)) {
      return null;
    }
    return Arrays.stream(value.split(",")).map(String::trim).filter(StringUtils::hasText).toList();
  }

  private LocalDate parseDate(String dateStr) {
    if (!StringUtils.hasText(dateStr)) {
      return null;
    }

    try {
      return LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      log.warn("Invalid date format: {}", dateStr);
      return null;
    }
  }
}
