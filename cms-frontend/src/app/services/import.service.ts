import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImportRequest, ImportJob, ProviderInfo } from '../models/import.model';

@Injectable({
  providedIn: 'root'
})
export class ImportService {
  private readonly API_URL = 'http://localhost:8078/api/v1';

  constructor(private http: HttpClient) { }

  importFromProvider(provider: string, request: ImportRequest): Observable<ImportJob> {
    return this.http.post<ImportJob>(`${this.API_URL}/import/${provider}`, request);
  }

  getImportJobStatus(jobId: string): Observable<ImportJob> {
    return this.http.get<ImportJob>(`${this.API_URL}/import/job/${jobId}`);
  }

  cancelImportJob(jobId: string, userEmail: string): Observable<ImportJob> {
    return this.http.post<ImportJob>(`${this.API_URL}/import/job/${jobId}/cancel`, { userEmail });
  }

  getAllImportJobs(): Observable<ImportJob[]> {
    return this.http.get<ImportJob[]>(`${this.API_URL}/import/jobs`);
  }

  getAvailableProviders(): Observable<{ [key: string]: ProviderInfo }> {
    return this.http.get<{ [key: string]: ProviderInfo }>(`${this.API_URL}/import/providers`);
  }

  checkProviderHealth(provider: string): Observable<{ status: string; message: string }> {
    return this.http.get<{ status: string; message: string }>(`${this.API_URL}/import/providers/${provider}/health`);
  }
} 