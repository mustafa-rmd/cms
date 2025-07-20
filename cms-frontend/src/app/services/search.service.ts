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

  advancedSearch(searchRequest: SearchRequest): Observable<SearchResponse> {
    return this.http.post<SearchResponse>(`${this.API_URL}/search/advanced`, searchRequest);
  }

  getShowById(showId: string): Observable<ShowSearchDto> {
    return this.http.get<ShowSearchDto>(`${this.API_URL}/search/show/${showId}`);
  }

  getPopularShows(page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/search/popular`, { params });
  }

  getRecentShows(page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/search/recent`, { params });
  }

  getShowsByType(type: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/search/type/${type}`, { params });
  }

  getShowsByTag(tag: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/search/tag/${tag}`, { params });
  }

  getShowsByCategory(category: string, page: number = 0, size: number = 20): Observable<SearchResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<SearchResponse>(`${this.API_URL}/search/category/${category}`, { params });
  }
} 