import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { ShowSearchDto } from '../../models/show.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  popularShows: ShowSearchDto[] = [];
  recentShows: ShowSearchDto[] = [];
  loading = true;

  constructor(private searchService: SearchService) {
    this.loadHomeData();
  }

  private loadHomeData(): void {
    this.loading = true;
    
    // Load popular shows
    this.searchService.getPopularShows(0, 6).subscribe({
      next: (response) => {
        this.popularShows = response.content;
      },
      error: (error) => {
        console.error('Error loading popular shows:', error);
      }
    });

    // Load recent shows
    this.searchService.getRecentShows(0, 6).subscribe({
      next: (response) => {
        this.recentShows = response.content;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading recent shows:', error);
        this.loading = false;
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
    return new Date(dateString).toLocaleDateString();
  }
} 