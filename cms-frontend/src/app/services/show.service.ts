import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Show, ShowCreateUpdateDto } from '../models/show.model';

@Injectable({
  providedIn: 'root'
})
export class ShowService {
  private readonly API_URL = 'http://localhost:8078/api/v1';

  constructor(private http: HttpClient) { }

  getShows(pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows`, { params });
  }

  getShowById(id: string): Observable<Show> {
    return this.http.get<Show>(`${this.API_URL}/shows/${id}`);
  }

  createShow(show: ShowCreateUpdateDto): Observable<Show> {
    return this.http.post<Show>(`${this.API_URL}/shows`, show);
  }

  updateShow(id: string, show: ShowCreateUpdateDto): Observable<Show> {
    return this.http.put<Show>(`${this.API_URL}/shows/${id}`, show);
  }

  deleteShow(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/shows/${id}`);
  }

  deleteShows(ids: string[]): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/shows/batch`, { body: ids });
  }

  getShowsByType(type: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/type/${type}`, { params });
  }

  getShowsByLanguage(language: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/language/${language}`, { params });
  }

  getShowsByProvider(provider: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/provider/${provider}`, { params });
  }

  searchShowsByTitle(title: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/search/title/${title}`, { params });
  }

  getShowsByPublishedAt(publishedAt: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/published/${publishedAt}`, { params });
  }

  getShowsByCreatedBy(userEmail: string, pageNumber: number = 0, pageSize: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('pageNumber', pageNumber.toString())
      .set('pageSize', pageSize.toString());
    
    return this.http.get<any>(`${this.API_URL}/shows/creator/${userEmail}`, { params });
  }

  getDistinctFields(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/shows/distinct-fields`);
  }

  getShowStatistics(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/shows/statistics`);
  }
} 