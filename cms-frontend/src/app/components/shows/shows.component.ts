import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ShowService } from '../../services/show.service';
import { Show } from '../../models/show.model';

@Component({
  selector: 'app-shows',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './shows.component.html',
  styleUrl: './shows.component.scss'
})
export class ShowsComponent implements OnInit {
  shows: Show[] = [];
  loading = true;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 10;

  constructor(private showService: ShowService) {}

  ngOnInit(): void {
    this.loadShows();
  }

  loadShows(): void {
    this.loading = true;
    this.showService.getShows(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.shows = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading shows:', error);
        this.loading = false;
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadShows();
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
    return new Date(dateString).toLocaleDateString();
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPages = Math.min(5, this.totalPages);
    
    for (let i = 0; i < maxPages; i++) {
      pages.push(i);
    }
    
    return pages;
  }
} 