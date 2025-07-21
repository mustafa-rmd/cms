import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { AuthService } from '../../services/auth.service';
import { ShowSearchDto, SearchResponse } from '../../models/show.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  popularShows: ShowSearchDto[] = [];
  recentShows: ShowSearchDto[] = [];
  loading = true;
  private completedRequests = 0;
  private totalRequests = 2; // popular shows + recent shows

  constructor(
    private searchService: SearchService,
    private authService: AuthService
  ) {}

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  hasRole(role: string): boolean {
    return this.authService.hasRole(role);
  }

  ngOnInit(): void {
    this.loading = true;
    this.completedRequests = 0;
    
    // Load popular shows
    this.searchService.getPopularShows(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.popularShows = response.results;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading popular shows:', error);
        this.checkLoadingComplete();
      }
    });

    // Load recent shows
    this.searchService.getRecentShows(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.recentShows = response.results;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading recent shows:', error);
        this.checkLoadingComplete();
      }
    });
  }

  private checkLoadingComplete(): void {
    this.completedRequests++;
    if (this.completedRequests >= this.totalRequests) {
      this.loading = false;
    }
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
} 