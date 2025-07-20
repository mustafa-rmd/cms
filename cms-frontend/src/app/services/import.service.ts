import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ImportRequest {
  topic: string;
  startDate: string;
  endDate: string;
  skipDuplicates?: boolean;
  batchSize?: number;
}

export interface ImportJob {
  jobId: string;
  provider: string;
  startDate: string;
  endDate: string;
  status: ImportJobStatus;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
  createdBy: string;
  totalItems?: number;
  processedItems?: number;
  successfulItems?: number;
  failedItems?: number;
  errorMessage?: string;
  errorDetails?: string;
  statusMessage?: string;
}

export enum ImportJobStatus {
  PENDING = 'PENDING',
  QUEUED = 'QUEUED',
  IN_PROGRESS = 'IN_PROGRESS',
  PROCESSING = 'PROCESSING',
  FETCHING = 'FETCHING',
  SAVING = 'SAVING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  RETRYING = 'RETRYING'
}

export interface ProviderInfo {
  available: boolean;
  maxBatchSize: number;
  rateLimitPerMinute: number;
}

export interface ImportStatistics {
  totalJobs: number;
  activeJobs: number;
  completedJobs: number;
  failedJobs: number;
  queuedJobs: number;
  jobsByProvider: { [key: string]: number };
}

@Injectable({
  providedIn: 'root'
})
export class ImportService {
  private apiUrl = `${environment.apiUrl}/import/async`;

  constructor(private http: HttpClient) {}

  // Start async import
  startImport(provider: string, request: ImportRequest): Observable<ImportJob> {
    return this.http.post<ImportJob>(`${this.apiUrl}/${provider}`, request);
  }

  // Get import job status
  getImportJobStatus(jobId: string): Observable<ImportJob> {
    return this.http.get<ImportJob>(`${this.apiUrl}/jobs/${jobId}`);
  }

  // Cancel import job
  cancelImportJob(jobId: string): Observable<ImportJob> {
    return this.http.post<ImportJob>(`${this.apiUrl}/jobs/${jobId}/cancel`, {});
  }

  // Retry failed import job
  retryImportJob(jobId: string): Observable<ImportJob> {
    return this.http.post<ImportJob>(`${this.apiUrl}/jobs/${jobId}/retry`, {});
  }

  // Get user's import jobs
  getMyImportJobs(): Observable<ImportJob[]> {
    return this.http.get<ImportJob[]>(`${this.apiUrl}/jobs/my`);
  }

  // Get all import jobs (admin only)
  getAllImportJobs(): Observable<ImportJob[]> {
    return this.http.get<ImportJob[]>(`${this.apiUrl}/jobs`);
  }

  // Get import jobs by status (admin only)
  getImportJobsByStatus(status?: ImportJobStatus): Observable<ImportJob[]> {
    const params: any = {};
    if (status) {
      params.status = status;
    }
    return this.http.get<ImportJob[]>(`${this.apiUrl}/jobs`, { params });
  }

  // Get import statistics (admin only)
  getImportStatistics(): Observable<ImportStatistics> {
    return this.http.get<ImportStatistics>(`${this.apiUrl}/stats`);
  }

  // Get available providers
  getAvailableProviders(): Observable<{ [key: string]: ProviderInfo }> {
    return this.http.get<{ [key: string]: ProviderInfo }>(`${this.apiUrl}/providers`);
  }

  // Utility methods
  isJobActive(job: ImportJob): boolean {
    return job.status === ImportJobStatus.QUEUED ||
           job.status === ImportJobStatus.IN_PROGRESS ||
           job.status === ImportJobStatus.PROCESSING ||
           job.status === ImportJobStatus.FETCHING ||
           job.status === ImportJobStatus.SAVING ||
           job.status === ImportJobStatus.RETRYING;
  }

  isJobCompleted(job: ImportJob): boolean {
    return job.status === ImportJobStatus.COMPLETED ||
           job.status === ImportJobStatus.FAILED ||
           job.status === ImportJobStatus.CANCELLED;
  }

  getJobProgress(job: ImportJob): number {
    if (!job.totalItems || job.totalItems === 0) {
      return 0;
    }
    return Math.round((job.processedItems || 0) / job.totalItems * 100);
  }

  getStatusDisplayName(status: ImportJobStatus): string {
    const statusMap: { [key in ImportJobStatus]: string } = {
      [ImportJobStatus.PENDING]: 'في الانتظار',
      [ImportJobStatus.QUEUED]: 'في قائمة الانتظار',
      [ImportJobStatus.IN_PROGRESS]: 'قيد التنفيذ',
      [ImportJobStatus.PROCESSING]: 'قيد المعالجة',
      [ImportJobStatus.FETCHING]: 'جاري الجلب',
      [ImportJobStatus.SAVING]: 'جاري الحفظ',
      [ImportJobStatus.COMPLETED]: 'مكتمل',
      [ImportJobStatus.FAILED]: 'فشل',
      [ImportJobStatus.CANCELLED]: 'ملغي',
      [ImportJobStatus.RETRYING]: 'إعادة المحاولة'
    };
    return statusMap[status] || status;
  }

  getStatusBadgeClass(status: ImportJobStatus): string {
    const classMap: { [key in ImportJobStatus]: string } = {
      [ImportJobStatus.PENDING]: 'bg-secondary',
      [ImportJobStatus.QUEUED]: 'bg-info',
      [ImportJobStatus.IN_PROGRESS]: 'bg-primary',
      [ImportJobStatus.PROCESSING]: 'bg-primary',
      [ImportJobStatus.FETCHING]: 'bg-warning',
      [ImportJobStatus.SAVING]: 'bg-warning',
      [ImportJobStatus.COMPLETED]: 'bg-success',
      [ImportJobStatus.FAILED]: 'bg-danger',
      [ImportJobStatus.CANCELLED]: 'bg-secondary',
      [ImportJobStatus.RETRYING]: 'bg-warning'
    };
    return classMap[status] || 'bg-secondary';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('ar-SA', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDuration(startDate: string, endDate?: string): string {
    const start = new Date(startDate);
    const end = endDate ? new Date(endDate) : new Date();
    const diffMs = end.getTime() - start.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffDays > 0) {
      return `${diffDays} يوم`;
    } else if (diffHours > 0) {
      return `${diffHours} ساعة`;
    } else if (diffMins > 0) {
      return `${diffMins} دقيقة`;
    } else {
      return 'أقل من دقيقة';
    }
  }
} 