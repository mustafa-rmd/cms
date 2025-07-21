export interface Show {
  id: string;
  type: 'podcast' | 'documentary';
  title: string;
  description: string;
  language: string;
  durationSec: number;
  publishedAt: string;
  createdDate: string;
  updatedDate: string;
  createdBy: string;
  updatedBy: string;
  provider: 'internal' | 'vimeo' | 'youtube';
}

export interface ShowCreateUpdateDto {
  type: 'podcast' | 'documentary';
  title: string;
  description: string;
  language: string;
  durationSec: number;
  publishedAt: string;
}

export interface ShowSearchDto {
  showId: string;
  title: string;
  description: string;
  type: 'podcast' | 'documentary';
  language: string;
  durationSec: number;
  publishedAt: string;
  provider: string;
  externalId?: string;
  tags?: string[];
  categories?: string[];
  thumbnailUrl?: string;
  streamUrl?: string;
  viewCount?: number;
  rating?: number;
  createdDate: string;
  updatedDate: string;
  createdBy: string;
  updatedBy: string;
  score?: number;
  highlights?: string[];
}

export interface SearchRequest {
  query?: string;
  type?: string;
  language?: string;
  provider?: string;
  tags?: string;
  categories?: string;
  minDuration?: number;
  maxDuration?: number;
  publishedAfter?: string;
  publishedBefore?: string;
  minRating?: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  page?: number;
  size?: number;
  highlight?: boolean;
  fuzzy?: boolean;
}

export interface SearchResponse {
  results: ShowSearchDto[];
  totalResults: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
  executionTimeMs: number;
  appliedFilters?: Record<string, any>;
  metadata?: {
    originalQuery?: string;
    processedQuery?: string;
    searchType?: string;
    fuzzyApplied?: boolean;
    highlightApplied?: boolean;
  };
  suggestions?: string[];
  aggregations?: Record<string, Record<string, number>>;
} 