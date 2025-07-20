package com.thmanyah.services.cms.controllers.impl;

import com.thmanyah.services.cms.controllers.ShowControllerV1;
import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import com.thmanyah.services.cms.model.dto.ShowDto;
import com.thmanyah.services.cms.services.AuthorizationService;
import com.thmanyah.services.cms.services.ShowService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class ShowController implements ShowControllerV1 {

  private final ShowService showService;
  private final AuthorizationService authorizationService;

  @Override
  @GetMapping("/shows")
  public ResponseEntity<Page<ShowDto>> getShows(
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting shows - page: {}, size: {}", pageNumber, pageSize);
    return ResponseEntity.ok(showService.getAllShows(pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/{id}")
  public ResponseEntity<ShowDto> getShowById(@PathVariable UUID id) {
    log.info("Getting show by ID: {}", id);
    return ResponseEntity.ok(showService.getShowDtoById(id));
  }

  @Override
  @PostMapping("/shows")
  public ResponseEntity<ShowDto> createShow(
      @Valid @RequestBody ShowCreateUpdateDto showCreateUpdateDto) {

    authorizationService.requireShowManagement("create show");
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info("Creating show with title: {} by user: {}", showCreateUpdateDto.getTitle(), userEmail);
    ShowDto createdShow = showService.createShow(userEmail, showCreateUpdateDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdShow);
  }

  @Override
  @PutMapping("/shows/{id}")
  public ResponseEntity<ShowDto> updateShow(
      @PathVariable UUID id, @Valid @RequestBody ShowCreateUpdateDto showCreateUpdateDto) {

    authorizationService.requireShowManagement("update show");
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info("Updating show with ID: {} by user: {}", id, userEmail);
    ShowDto updatedShow = showService.updateShow(userEmail, id, showCreateUpdateDto);
    return ResponseEntity.ok(updatedShow);
  }

  @Override
  @DeleteMapping("/shows/{id}")
  public ResponseEntity<Void> deleteShow(@PathVariable UUID id) {

    authorizationService.requireShowDeletion("delete show");
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info("Deleting show with ID: {} by user: {}", id, userEmail);
    showService.deleteShow(userEmail, id);
    return ResponseEntity.noContent().build();
  }

  @Override
  @DeleteMapping("/shows")
  public ResponseEntity<Void> deleteShows(@RequestBody Set<UUID> ids) {

    authorizationService.requireShowDeletion("delete multiple shows");
    String userEmail = authorizationService.getCurrentUserEmail();

    log.info("Deleting {} shows by user: {}", ids.size(), userEmail);
    showService.deleteShows(userEmail, ids);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping("/shows/type/{type}")
  public ResponseEntity<Page<ShowDto>> getShowsByType(
      @PathVariable String type,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting shows by type: {} - page: {}, size: {}", type, pageNumber, pageSize);
    return ResponseEntity.ok(showService.getShowsByType(type, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/language/{language}")
  public ResponseEntity<Page<ShowDto>> getShowsByLanguage(
      @PathVariable String language,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting shows by language: {} - page: {}, size: {}", language, pageNumber, pageSize);
    return ResponseEntity.ok(showService.getShowsByLanguage(language, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/provider/{provider}")
  public ResponseEntity<Page<ShowDto>> getShowsByProvider(
      @PathVariable String provider,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting shows by provider: {} - page: {}, size: {}", provider, pageNumber, pageSize);
    return ResponseEntity.ok(showService.getShowsByProvider(provider, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/search")
  public ResponseEntity<Page<ShowDto>> searchShowsByTitle(
      @RequestParam String title,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Searching shows by title: {} - page: {}, size: {}", title, pageNumber, pageSize);
    return ResponseEntity.ok(showService.searchShowsByTitle(title, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/published/{publishedAt}")
  public ResponseEntity<Page<ShowDto>> getShowsByPublishedAt(
      @PathVariable LocalDate publishedAt,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info(
        "Getting shows by published date: {} - page: {}, size: {}",
        publishedAt,
        pageNumber,
        pageSize);
    return ResponseEntity.ok(showService.getShowsByPublishedAt(publishedAt, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/creator/{userEmail}")
  public ResponseEntity<Page<ShowDto>> getShowsByCreatedBy(
      @PathVariable String userEmail,
      @RequestParam(defaultValue = "0", required = false) int pageNumber,
      @RequestParam(defaultValue = "10", required = false) int pageSize) {

    log.info("Getting shows created by: {} - page: {}, size: {}", userEmail, pageNumber, pageSize);
    return ResponseEntity.ok(showService.getShowsByCreatedBy(userEmail, pageNumber, pageSize));
  }

  @Override
  @GetMapping("/shows/filtering-values")
  public ResponseEntity<Map<String, Map<String, Object>>> getDistinctFields() {
    log.info("Getting distinct fields for filtering");
    return ResponseEntity.ok(showService.getDistinctFields());
  }

  @Override
  @GetMapping("/shows/statistics")
  public ResponseEntity<Map<String, Object>> getShowStatistics() {
    log.info("Getting show statistics");
    return ResponseEntity.ok(showService.getShowStatistics());
  }
}
