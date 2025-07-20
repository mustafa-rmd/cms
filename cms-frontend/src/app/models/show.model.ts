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

export interface ShowSearchDto extends Show {
  tags?: string[];
  categories?: string[];
  rating?: number;
  viewCount?: number;
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
  content: ShowSearchDto[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
} 