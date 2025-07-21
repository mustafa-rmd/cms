import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SearchRequest, SearchResponse, ShowSearchDto } from '../models/show.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private readonly API_URL = 'http://localhost:8079/api/v1';

  constructor(private http: HttpClient) { }

  // Basic Search Operations
  searchShows(
    query: string,
    type?: string,
    language?: string,
    provider?: string,
    tags?: string,
    categories?: string,
    minDuration?: number,
    maxDuration?: number,
    publishedAfter?: string,
    publishedBefore?: string,
    minRating?: number,
    sortBy?: string,
    sortDirection: 'asc' | 'desc' = 'desc',
    page: number = 0,
    size: number = 20,
    highlight: boolean = true,
    fuzzy: boolean = true
  ): Observable<SearchResponse> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortDirection', sortDirection)
      .set('highlight', highlight.toString())
      .set('fuzzy', fuzzy.toString());

    if (query) params = params.set('query', query);
    if (type) params = params.set('type', type);
    if (language) params = params.set('language', language);
    if (provider) params = params.set('provider', provider);
    if (tags) params = params.set('tags', tags);
    if (categories) params = params.set('categories', categories);
    if (minDuration) params = params.set('minDuration', minDuration.toString());
    if (maxDuration) params = params.set('maxDuration', maxDuration.toString());
    if (publishedAfter) params = params.set('publishedAfter', publishedAfter);
    if (publishedBefore) params = params.set('publishedBefore', publishedBefore);
    if (minRating) params = params.set('minRating', minRating.toString());
    if (sortBy) params = params.set('sortBy', sortBy);

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  // Advanced Search with POST request
  advancedSearch(searchRequest: SearchRequest): Observable<SearchResponse> {
    return this.http.post<SearchResponse>(`${this.API_URL}/search`, searchRequest);
  }

  // Browse all content without query
  browseAllContent(
    sortBy: string = 'createdDate',
    sortDirection: 'asc' | 'desc' = 'desc',
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  // Content Discovery
  getShowById(showId: string): Observable<ShowSearchDto> {
    return this.http.get<ShowSearchDto>(`${this.API_URL}/shows/${showId}`);
  }

  getPopularShows(page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/shows/popular`, { params });
  }

  getRecentShows(page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/shows/recent`, { params });
  }

  // Content Filtering by Type
  getShowsByType(type: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/shows/type/${type}`, { params });
  }

  getPodcasts(page: number = 0, size: number = 20): Observable<SearchResponse> {
    return this.getShowsByType('podcast', page, size);
  }

  getDocumentaries(page: number = 0, size: number = 20): Observable<SearchResponse> {
    return this.getShowsByType('documentary', page, size);
  }

  // Content Filtering by Tags and Categories
  getShowsByTag(tag: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/shows/tag/${tag}`, { params });
  }

  getShowsByCategory(category: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/shows/category/${category}`, { params });
  }

  // Search with specific filters
  searchByDateRange(
    query: string,
    publishedAfter: string,
    publishedBefore: string,
    minRating?: number,
    sortBy: string = 'publishedAt',
    sortDirection: 'asc' | 'desc' = 'desc',
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    let params = new HttpParams()
      .set('query', query)
      .set('publishedAfter', publishedAfter)
      .set('publishedBefore', publishedBefore)
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page.toString())
      .set('size', size.toString());

    if (minRating) params = params.set('minRating', minRating.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  searchByDuration(
    query: string,
    minDuration: number,
    maxDuration: number,
    sortBy: string = 'durationSec',
    sortDirection: 'asc' | 'desc' = 'asc',
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('minDuration', minDuration.toString())
      .set('maxDuration', maxDuration.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  searchByProvider(
    query: string,
    provider: string,
    sortBy: string = 'relevance',
    sortDirection: 'asc' | 'desc' = 'desc',
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('provider', provider)
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  // Fuzzy search with typos
  fuzzySearch(
    query: string,
    highlight: boolean = true,
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('fuzzy', 'true')
      .set('highlight', highlight.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }

  // Sort by different fields
  searchWithSorting(
    query: string,
    sortBy: 'relevance' | 'rating' | 'viewCount' | 'publishedAt' | 'durationSec' | 'createdDate',
    sortDirection: 'asc' | 'desc' = 'desc',
    page: number = 0,
    size: number = 20
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('query', query)
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponse>(`${this.API_URL}/search`, { params });
  }
} 