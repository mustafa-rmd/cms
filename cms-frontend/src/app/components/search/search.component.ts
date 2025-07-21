import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { AuthService } from '../../services/auth.service';
import { SearchResponse, ShowSearchDto } from '../../models/show.model';

type ViewMode = 'cards' | 'table';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent implements OnInit {
  searchForm: FormGroup;
  searchResults: ShowSearchDto[] = [];
  loading = false;
  currentPage = 1;
  totalPages = 0;
  totalElements = 0;
  totalResults = 0;
  pageSize = 20;
  hasSearched = false;
  searched = false;
  viewMode: ViewMode = 'cards';

  constructor(
    private fb: FormBuilder,
    private searchService: SearchService,
    private authService: AuthService
  ) {
    this.searchForm = this.fb.group({
      query: [''],
      type: [''],
      language: [''],
      provider: [''],
      tags: [''],
      categories: [''],
      minDuration: [''],
      maxDuration: [''],
      publishedAfter: [''],
      publishedBefore: [''],
      minRating: [''],
      sortBy: ['relevance'],
      sortDirection: ['desc'],
      pageSize: [20],
      highlight: [true],
      fuzzy: [true]
    });
  }

  ngOnInit(): void {
    // Load popular shows on initial load
    this.loadPopularShows();
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  setViewMode(mode: ViewMode): void {
    this.viewMode = mode;
  }

  onSearch(): void {
    if (this.searchForm.valid) {
      this.loading = true;
      this.currentPage = 1;
      this.hasSearched = true;
      this.searched = true;

      const formValue = this.searchForm.value;
      
      // Convert duration from minutes to seconds
      const minDuration = formValue.minDuration ? formValue.minDuration * 60 : undefined;
      const maxDuration = formValue.maxDuration ? formValue.maxDuration * 60 : undefined;
      
      this.searchService.searchShows(
        formValue.query,
        formValue.type,
        formValue.language,
        formValue.provider,
        formValue.tags,
        formValue.categories,
        minDuration,
        maxDuration,
        formValue.publishedAfter,
        formValue.publishedBefore,
        formValue.minRating,
        formValue.sortBy,
        formValue.sortDirection,
        this.currentPage - 1, // API expects 0-based index
        formValue.pageSize || this.pageSize,
        formValue.highlight,
        formValue.fuzzy
      ).subscribe({
        next: (response: SearchResponse) => {
          this.searchResults = response.results;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalResults;
          this.totalResults = response.totalResults;
          this.loading = false;
        },
        error: (error) => {
          console.error('Search error:', error);
          this.loading = false;
        }
      });
    }
  }

  onPageChange(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.onSearch();
    }
  }

  loadPopularShows(): void {
    this.loading = true;
    this.searchService.getPopularShows(0, 12).subscribe({
      next: (response: SearchResponse) => {
        this.searchResults = response.results;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalResults;
        this.totalResults = response.totalResults;
        this.loading = false;
        this.hasSearched = false;
        this.searched = false;
      },
      error: (error) => {
        console.error('Error loading popular shows:', error);
        this.loading = false;
      }
    });
  }

  loadRecentShows(): void {
    this.loading = true;
    this.searchService.getRecentShows(0, 12).subscribe({
      next: (response: SearchResponse) => {
        this.searchResults = response.results;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalResults;
        this.totalResults = response.totalResults;
        this.loading = false;
        this.hasSearched = false;
        this.searched = false;
      },
      error: (error) => {
        console.error('Error loading recent shows:', error);
        this.loading = false;
      }
    });
  }

  loadPodcasts(): void {
    this.loading = true;
    this.searchService.getPodcasts(0, 12).subscribe({
      next: (response: SearchResponse) => {
        this.searchResults = response.results;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalResults;
        this.totalResults = response.totalResults;
        this.loading = false;
        this.hasSearched = false;
        this.searched = false;
      },
      error: (error) => {
        console.error('Error loading podcasts:', error);
        this.loading = false;
      }
    });
  }

  loadDocumentaries(): void {
    this.loading = true;
    this.searchService.getDocumentaries(0, 12).subscribe({
      next: (response: SearchResponse) => {
        this.searchResults = response.results;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalResults;
        this.totalResults = response.totalResults;
        this.loading = false;
        this.hasSearched = false;
        this.searched = false;
      },
      error: (error) => {
        console.error('Error loading documentaries:', error);
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchForm.reset({
      sortBy: 'relevance',
      sortDirection: 'desc',
      pageSize: 20,
      highlight: true,
      fuzzy: true
    });
    this.hasSearched = false;
    this.searched = false;
    this.loadPopularShows();
  }

  formatDuration(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m`;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('ar-SA');
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    let startPage = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(this.totalPages, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }
} 