package com.thmanyah.services.cmsdiscovery.repository;

import com.thmanyah.services.cmsdiscovery.model.ShowDocument;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRepository extends ElasticsearchRepository<ShowDocument, String> {

  /** Find show by original show ID */
  Optional<ShowDocument> findByShowId(UUID showId);

  /** Find all public and active shows */
  Page<ShowDocument> findByIsPublicTrueAndIsActiveTrue(Pageable pageable);

  /** Find shows by type (podcast, documentary) */
  Page<ShowDocument> findByTypeAndIsPublicTrueAndIsActiveTrue(String type, Pageable pageable);

  /** Find shows by language */
  Page<ShowDocument> findByLanguageAndIsPublicTrueAndIsActiveTrue(
      String language, Pageable pageable);

  /** Find shows by provider */
  Page<ShowDocument> findByProviderAndIsPublicTrueAndIsActiveTrue(
      String provider, Pageable pageable);

  /** Find shows published after a certain date */
  Page<ShowDocument> findByPublishedAtAfterAndIsPublicTrueAndIsActiveTrue(
      LocalDate date, Pageable pageable);

  /** Find shows by duration range */
  Page<ShowDocument> findByDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
      Integer minDuration, Integer maxDuration, Pageable pageable);

  /** Full-text search in title and description */
  @Query(
      """
      {
        "bool": {
          "must": [
            {
              "multi_match": {
                "query": "?0",
                "fields": ["title^3", "description^2", "searchText"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }
            },
            {
              "term": {
                "isPublic": true
              }
            },
            {
              "term": {
                "isActive": true
              }
            }
          ]
        }
      }
      """)
  Page<ShowDocument> searchByText(String query, Pageable pageable);

  /** Search with filters */
  @Query(
      """
      {
        "bool": {
          "must": [
            {
              "multi_match": {
                "query": "?0",
                "fields": ["title^3", "description^2", "searchText"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }
            },
            {
              "term": {
                "isPublic": true
              }
            },
            {
              "term": {
                "isActive": true
              }
            }
          ],
          "filter": [
            ?1
          ]
        }
      }
      """)
  Page<ShowDocument> searchByTextWithFilters(String query, String filters, Pageable pageable);

  /** Find shows by tags */
  Page<ShowDocument> findByTagsContainingAndIsPublicTrueAndIsActiveTrue(
      String tag, Pageable pageable);

  /** Find shows by categories */
  Page<ShowDocument> findByCategoriesContainingAndIsPublicTrueAndIsActiveTrue(
      String category, Pageable pageable);

  /** Find popular shows (by view count) */
  Page<ShowDocument> findByIsPublicTrueAndIsActiveTrueOrderByViewCountDesc(Pageable pageable);

  /** Find recent shows */
  Page<ShowDocument> findByIsPublicTrueAndIsActiveTrueOrderByCreatedDateDesc(Pageable pageable);

  /** Find highly rated shows */
  Page<ShowDocument> findByIsPublicTrueAndIsActiveTrueAndRatingGreaterThanEqualOrderByRatingDesc(
      Double minRating, Pageable pageable);

  /** Count shows by type */
  long countByTypeAndIsPublicTrueAndIsActiveTrue(String type);

  /** Count shows by provider */
  long countByProviderAndIsPublicTrueAndIsActiveTrue(String provider);

  /** Find shows with multiple filters - type and language */
  Page<ShowDocument> findByTypeAndLanguageAndIsPublicTrueAndIsActiveTrue(
      String type, String language, Pageable pageable);

  /** Find shows with type, language and duration range */
  Page<ShowDocument> findByTypeAndLanguageAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
      String type, String language, Integer minDuration, Integer maxDuration, Pageable pageable);

  /** Find shows with type and duration range */
  Page<ShowDocument> findByTypeAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
      String type, Integer minDuration, Integer maxDuration, Pageable pageable);

  /** Find shows with language and duration range */
  Page<ShowDocument> findByLanguageAndDurationSecBetweenAndIsPublicTrueAndIsActiveTrue(
      String language, Integer minDuration, Integer maxDuration, Pageable pageable);

  /** Find shows with published date range */
  Page<ShowDocument> findByPublishedAtBetweenAndIsPublicTrueAndIsActiveTrue(
      LocalDate startDate, LocalDate endDate, Pageable pageable);

  /** Find shows with rating filter */
  Page<ShowDocument> findByRatingGreaterThanEqualAndIsPublicTrueAndIsActiveTrue(
      Double minRating, Pageable pageable);
}
