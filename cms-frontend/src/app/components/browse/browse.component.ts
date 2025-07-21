import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { AuthService } from '../../services/auth.service';
import { SearchResponse, ShowSearchDto } from '../../models/show.model';

type ViewMode = 'cards' | 'table';

@Component({
  selector: 'app-browse',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './browse.component.html',
  styleUrl: './browse.component.scss'
})
export class BrowseComponent implements OnInit {
  // Content sections
  popularShows: ShowSearchDto[] = [];
  recentShows: ShowSearchDto[] = [];
  podcasts: ShowSearchDto[] = [];
  documentaries: ShowSearchDto[] = [];
  
  // Loading states
  loading = {
    popular: true,
    recent: true,
    podcasts: true,
    documentaries: true
  };

  // View mode
  viewMode: ViewMode = 'cards';

  // Popular tags and categories
  popularTags = [
    'technology', 'science', 'education', 'history', 'culture',
    'business', 'health', 'environment', 'politics', 'art'
  ];

  popularCategories = [
    'Science', 'Technology', 'Education', 'History', 'Culture',
    'Business', 'Health', 'Environment', 'Politics', 'Art'
  ];

  constructor(
    private searchService: SearchService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAllContent();
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  setViewMode(mode: ViewMode): void {
    this.viewMode = mode;
  }

  private loadAllContent(): void {
    // Load popular shows
    this.searchService.getPopularShows(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.popularShows = response.results;
        this.loading.popular = false;
      },
      error: (error) => {
        console.error('Error loading popular shows:', error);
        this.loading.popular = false;
      }
    });

    // Load recent shows
    this.searchService.getRecentShows(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.recentShows = response.results;
        this.loading.recent = false;
      },
      error: (error) => {
        console.error('Error loading recent shows:', error);
        this.loading.recent = false;
      }
    });

    // Load podcasts
    this.searchService.getPodcasts(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.podcasts = response.results;
        this.loading.podcasts = false;
      },
      error: (error) => {
        console.error('Error loading podcasts:', error);
        this.loading.podcasts = false;
      }
    });

    // Load documentaries
    this.searchService.getDocumentaries(0, 6).subscribe({
      next: (response: SearchResponse) => {
        this.documentaries = response.results;
        this.loading.documentaries = false;
      },
      error: (error) => {
        console.error('Error loading documentaries:', error);
        this.loading.documentaries = false;
      }
    });
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

  getTypeLabel(type: string): string {
    return type === 'podcast' ? 'بودكاست' : 'فيلم وثائقي';
  }

  getTypeIcon(type: string): string {
    return type === 'podcast' ? 'bi-mic' : 'bi-camera-video';
  }

  getTypeColor(type: string): string {
    return type === 'podcast' ? 'primary' : 'success';
  }
} 