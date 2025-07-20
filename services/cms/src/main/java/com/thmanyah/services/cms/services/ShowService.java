package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowCreateUpdateDto;
import com.thmanyah.services.cms.model.dto.ShowDto;
import com.thmanyah.services.cms.model.dto.ShowExternalDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ShowService {

  /**
   * Create a new show
   *
   * @param userEmail Email of the user creating the show
   * @param showCreateUpdateDto Show data
   * @return Created show DTO
   */
  ShowDto createShow(String userEmail, ShowCreateUpdateDto showCreateUpdateDto);

  /**
   * Update an existing show
   *
   * @param userEmail Email of the user updating the show
   * @param id Show ID
   * @param showCreateUpdateDto Updated show data
   * @return Updated show DTO
   */
  ShowDto updateShow(String userEmail, UUID id, ShowCreateUpdateDto showCreateUpdateDto);

  /**
   * Get all shows with pagination
   *
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> getAllShows(int pageNumber, int pageSize);

  /**
   * Get shows by provider with pagination
   *
   * @param provider Provider name (e.g., "internal", "vimeo", "youtube")
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs filtered by provider
   */
  Page<ShowDto> getShowsByProvider(String provider, int pageNumber, int pageSize);

  /**
   * Get show by ID as DTO
   *
   * @param id Show ID
   * @return Show DTO
   */
  ShowDto getShowDtoById(UUID id);

  /**
   * Get show entity by ID
   *
   * @param id Show ID
   * @return Show entity
   */
  Show getShowById(UUID id);

  /**
   * Delete a show
   *
   * @param userEmail Email of the user deleting the show
   * @param id Show ID
   */
  void deleteShow(String userEmail, UUID id);

  /**
   * Delete multiple shows
   *
   * @param userEmail Email of the user deleting the shows
   * @param ids Set of show IDs
   */
  void deleteShows(String userEmail, Set<UUID> ids);

  /**
   * Get shows by type
   *
   * @param type Show type (podcast, documentary)
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> getShowsByType(String type, int pageNumber, int pageSize);

  /**
   * Get shows by language
   *
   * @param language Language code
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> getShowsByLanguage(String language, int pageNumber, int pageSize);

  /**
   * Get shows by published date
   *
   * @param publishedAt Published date
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> getShowsByPublishedAt(LocalDate publishedAt, int pageNumber, int pageSize);

  /**
   * Search shows by title
   *
   * @param title Title to search for
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> searchShowsByTitle(String title, int pageNumber, int pageSize);

  /**
   * Get shows created by a specific user
   *
   * @param userEmail User email
   * @param pageNumber Page number (0-based)
   * @param pageSize Page size
   * @return Page of show DTOs
   */
  Page<ShowDto> getShowsByCreatedBy(String userEmail, int pageNumber, int pageSize);

  /**
   * Get distinct field values for filtering
   *
   * @return Map of field names to their distinct values
   */
  Map<String, Map<String, Object>> getDistinctFields();

  /**
   * Get statistics about shows
   *
   * @return Map containing various statistics
   */
  Map<String, Object> getShowStatistics();

  /**
   * Save a show from external import
   *
   * @param userEmail Email of the user performing the import
   * @param externalDto External show data
   * @param skipDuplicates Whether to skip shows with duplicate titles
   * @return Saved show entity, or null if skipped due to duplicate
   */
  Show saveFromImport(String userEmail, ShowExternalDto externalDto, boolean skipDuplicates);

  /**
   * Save multiple shows from external import using batch processing
   *
   * @param userEmail Email of the user performing the import
   * @param externalDtos List of external show data
   * @param skipDuplicates Whether to skip shows with duplicate titles
   * @return List of saved show entities
   */
  List<Show> saveAllFromImport(
      String userEmail, List<ShowExternalDto> externalDtos, boolean skipDuplicates);

  /**
   * Check if a show with the given title already exists
   *
   * @param title Show title to check
   * @return true if exists, false otherwise
   */
  boolean existsByTitle(String title);
}
