import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { ShowService } from '../../../services/show.service';
import { Show } from '../../../models/show.model';

@Component({
  selector: 'app-show-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './show-detail.component.html',
  styleUrl: './show-detail.component.scss'
})
export class ShowDetailComponent implements OnInit {
  show: Show | null = null;
  loading = true;
  error: string | null = null;
  deleting = false;

  constructor(
    private showService: ShowService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadShow();
  }

  loadShow(): void {
    const showId = this.route.snapshot.paramMap.get('id');
    if (!showId) {
      this.error = 'معرف العرض غير صحيح';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.error = null;

    this.showService.getShowById(showId).subscribe({
      next: (show) => {
        this.show = show;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading show:', error);
        this.error = 'حدث خطأ أثناء تحميل بيانات العرض';
        this.loading = false;
      }
    });
  }

  formatDuration(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    
    if (hours > 0) {
      return `${hours}س ${minutes}د`;
    }
    return `${minutes}د`;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('ar-SA');
  }

  copyShowId(): void {
    if (this.show) {
      navigator.clipboard.writeText(this.show.id).then(() => {
        // You could add a toast notification here
        console.log('Show ID copied to clipboard');
      }).catch(err => {
        console.error('Failed to copy show ID:', err);
      });
    }
  }

  shareShow(): void {
    if (this.show && navigator.share) {
      navigator.share({
        title: this.show.title,
        text: this.show.description,
        url: window.location.href
      }).catch(err => {
        console.error('Error sharing show:', err);
      });
    } else {
      // Fallback: copy URL to clipboard
      navigator.clipboard.writeText(window.location.href).then(() => {
        console.log('Show URL copied to clipboard');
      }).catch(err => {
        console.error('Failed to copy show URL:', err);
      });
    }
  }

  deleteShow(): void {
    if (!this.show) return;

    if (confirm('هل أنت متأكد من حذف هذا العرض؟ لا يمكن التراجع عن هذا الإجراء.')) {
      this.deleting = true;
      
      this.showService.deleteShow(this.show.id).subscribe({
        next: () => {
          this.deleting = false;
          this.router.navigate(['/shows']);
        },
        error: (error) => {
          console.error('Error deleting show:', error);
          this.error = 'حدث خطأ أثناء حذف العرض';
          this.deleting = false;
        }
      });
    }
  }
} 