export interface ImportRequest {
  startDate: string;
  endDate: string;
  maxResults?: number;
}

export interface ImportJob {
  id: string;
  provider: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  startDate: string;
  endDate: string;
  maxResults: number;
  processedCount: number;
  successCount: number;
  errorCount: number;
  errorMessage?: string;
  createdDate: string;
  updatedDate: string;
  createdBy: string;
}

export interface ProviderInfo {
  name: string;
  displayName: string;
  description: string;
  isAvailable: boolean;
  healthStatus: 'HEALTHY' | 'UNHEALTHY' | 'UNKNOWN';
} 