import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { SearchResponse, ShowSearchDto } from '../../models/show.model';

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

  constructor(
    private fb: FormBuilder,
    private searchService: SearchService
  ) {
    this.searchForm = this.fb.group({
      query: [''],
      type: [''],
      language: [''],
      provider: [''],
      sortBy: ['relevance'],
      pageSize: [20]
    });
  }

  ngOnInit(): void {
    // Load popular shows on initial load
    this.loadPopularShows();
  }

  onSearch(): void {
    if (this.searchForm.valid) {
      this.loading = true;
      this.currentPage = 1;
      this.hasSearched = true;
      this.searched = true;

      const formValue = this.searchForm.value;
      
      this.searchService.searchShows(
        formValue.query,
        formValue.type,
        formValue.language,
        formValue.provider,
        '', // tags
        '', // categories
        undefined, // minDuration
        undefined, // maxDuration
        '', // publishedAfter
        '', // publishedBefore
        undefined, // minRating
        formValue.sortBy,
        'desc', // sortDirection
        this.currentPage - 1, // API expects 0-based index
        formValue.pageSize || this.pageSize
      ).subscribe({
        next: (response: SearchResponse) => {
          this.searchResults = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.totalResults = response.totalElements;
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
        this.searchResults = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.totalResults = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading popular shows:', error);
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchForm.reset({
      sortBy: 'relevance',
      pageSize: 20
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