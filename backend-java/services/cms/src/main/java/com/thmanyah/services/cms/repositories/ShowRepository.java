package com.thmanyah.services.cms.repositories;

import com.thmanyah.services.cms.model.Show;
import com.thmanyah.services.cms.model.dto.ShowDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID>, JpaSpecificationExecutor<Show> {

  // Common query fragment for fetching ShowDto
  String SHOW_DTO_QUERY =
      """
          SELECT new com.thmanyah.services.cms.model.dto.ShowDto(
              s.id,
              s.type,
              s.title,
              s.description,
              s.language,
              s.durationSec,
              s.publishedAt,
              s.createdDate,
              s.updatedDate,
              s.createdBy,
              s.updatedBy,
              s.provider)
          FROM Show s
      """;

  @Query(SHOW_DTO_QUERY + "ORDER BY s.createdDate DESC")
  Page<ShowDto> findAllShowsAsDto(Pageable pageable);

  @Query(SHOW_DTO_QUERY + "WHERE s.provider = :provider ORDER BY s.createdDate DESC")
  Page<ShowDto> findByProviderAsDto(@Param("provider") String provider, Pageable pageable);

  @Query(SHOW_DTO_QUERY + "WHERE s.id = :id")
  Optional<ShowDto> findShowDtoById(@Param("id") UUID id);

  @Query(SHOW_DTO_QUERY + "WHERE s.type = :type ORDER BY s.createdDate DESC")
  Page<ShowDto> findByType(@Param("type") String type, Pageable pageable);

  @Query(SHOW_DTO_QUERY + "WHERE s.language = :language ORDER BY s.createdDate DESC")
  Page<ShowDto> findByLanguage(@Param("language") String language, Pageable pageable);

  @Query(SHOW_DTO_QUERY + "WHERE s.publishedAt = :publishedAt ORDER BY s.createdDate DESC")
  Page<ShowDto> findByPublishedAt(@Param("publishedAt") LocalDate publishedAt, Pageable pageable);

  @Query(
      SHOW_DTO_QUERY
          + "WHERE s.publishedAt BETWEEN :startDate AND :endDate ORDER BY s.publishedAt DESC")
  Page<ShowDto> findByPublishedAtBetween(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable);

  @Query(
      SHOW_DTO_QUERY
          + "WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY s.createdDate DESC")
  Page<ShowDto> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

  @Query(
      SHOW_DTO_QUERY
          + "WHERE s.type = :type AND s.language = :language ORDER BY s.createdDate DESC")
  Page<ShowDto> findByTypeAndLanguage(
      @Param("type") String type, @Param("language") String language, Pageable pageable);

  @Query("SELECT DISTINCT s.type FROM Show s ORDER BY s.type")
  List<String> findDistinctTypes();

  @Query("SELECT DISTINCT s.language FROM Show s WHERE s.language IS NOT NULL ORDER BY s.language")
  List<String> findDistinctLanguages();

  @Query("SELECT COUNT(s) FROM Show s WHERE s.type = :type")
  long countByType(@Param("type") String type);

  @Query("SELECT COUNT(s) FROM Show s WHERE s.language = :language")
  long countByLanguage(@Param("language") String language);

  @Query("SELECT COUNT(s) FROM Show s WHERE s.publishedAt = :publishedAt")
  long countByPublishedAt(@Param("publishedAt") LocalDate publishedAt);

  // For write-heavy optimization - bulk operations
  @Query("SELECT s FROM Show s WHERE s.id IN :ids")
  List<Show> findByIdIn(@Param("ids") List<UUID> ids);

  // Check if title exists (for uniqueness validation)
  boolean existsByTitleIgnoreCase(String title);

  // Find shows created by specific user
  @Query(SHOW_DTO_QUERY + "WHERE s.createdBy = :userEmail ORDER BY s.createdDate DESC")
  Page<ShowDto> findByCreatedBy(@Param("userEmail") String userEmail, Pageable pageable);
}
