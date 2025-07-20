import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ImportService, ImportRequest, ImportJob, ImportJobStatus, ProviderInfo, ImportStatistics } from '../../services/import.service';
import { AuthService } from '../../services/auth.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-import',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './import.component.html',
  styleUrl: './import.component.scss'
})
export class ImportComponent implements OnInit, OnDestroy {
  // Form states
  isImportFormVisible = false;
  importForm: FormGroup;
  
  // Data
  providers: { [key: string]: ProviderInfo } = {};
  myJobs: ImportJob[] = [];
  allJobs: ImportJob[] = [];
  statistics: ImportStatistics | null = null;
  
  // UI states
  loading = false;
  loadingProviders = false;
  loadingJobs = false;
  loadingStats = false;
  
  // Error handling
  errorMessage = '';
  successMessage = '';
  
  // Auto refresh
  private destroy$ = new Subject<void>();
  private autoRefreshInterval: any;
  
  // Current filter
  currentFilter: 'my' | 'all' | 'active' | 'completed' | 'failed' = 'my';
  selectedStatus: ImportJobStatus | null = null;

  constructor(
    public importService: ImportService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.importForm = this.fb.group({
      provider: ['', Validators.required],
      topic: ['', [Validators.required, Validators.minLength(2)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      skipDuplicates: [true],
      batchSize: [10, [Validators.min(1), Validators.max(100)]]
    });
  }

  ngOnInit(): void {
    this.loadProviders();
    this.loadMyJobs();
    this.setupAutoRefresh();
    
    if (this.isAdmin()) {
      this.loadAllJobs();
      this.loadStatistics();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.autoRefreshInterval) {
      clearInterval(this.autoRefreshInterval);
    }
  }

  private setupAutoRefresh(): void {
    // Auto refresh active jobs every 10 seconds
    this.autoRefreshInterval = setInterval(() => {
      if (!this.loading) {
        this.refreshActiveJobs();
      }
    }, 10000);
  }

  loadProviders(): void {
    this.loadingProviders = true;
    this.importService.getAvailableProviders()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (providers) => {
          this.providers = providers;
          this.loadingProviders = false;
        },
        error: (error) => {
          console.error('Error loading providers:', error);
          this.errorMessage = 'فشل في تحميل مزودي المحتوى. يرجى المحاولة مرة أخرى.';
          this.loadingProviders = false;
        }
      });
  }

  loadMyJobs(): void {
    this.loadingJobs = true;
    this.importService.getMyImportJobs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (jobs) => {
          this.myJobs = jobs;
          this.loadingJobs = false;
        },
        error: (error) => {
          console.error('Error loading my jobs:', error);
          this.errorMessage = 'فشل في تحميل مهام الاستيراد. يرجى المحاولة مرة أخرى.';
          this.loadingJobs = false;
        }
      });
  }

  loadAllJobs(): void {
    if (!this.isAdmin()) return;
    
    this.loadingJobs = true;
    this.importService.getAllImportJobs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (jobs) => {
          this.allJobs = jobs;
          this.loadingJobs = false;
        },
        error: (error) => {
          console.error('Error loading all jobs:', error);
          this.errorMessage = 'فشل في تحميل جميع مهام الاستيراد. يرجى المحاولة مرة أخرى.';
          this.loadingJobs = false;
        }
      });
  }

  loadStatistics(): void {
    if (!this.isAdmin()) return;
    
    this.loadingStats = true;
    this.importService.getImportStatistics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.statistics = stats;
          this.loadingStats = false;
        },
        error: (error) => {
          console.error('Error loading statistics:', error);
          this.loadingStats = false;
        }
      });
  }

  refreshActiveJobs(): void {
    // Only refresh if we have active jobs
    const hasActiveJobs = [...this.myJobs, ...this.allJobs].some(job => 
      this.importService.isJobActive(job)
    );
    
    if (hasActiveJobs) {
      this.loadMyJobs();
      if (this.isAdmin()) {
        this.loadAllJobs();
      }
    }
  }

  showImportForm(): void {
    this.isImportFormVisible = true;
    this.clearMessages();
    this.importForm.reset({
      skipDuplicates: true,
      batchSize: 10
    });
  }

  hideImportForm(): void {
    this.isImportFormVisible = false;
    this.clearMessages();
  }

  startImport(): void {
    if (this.importForm.valid) {
      this.loading = true;
      const formValue = this.importForm.value;
      
      const request: ImportRequest = {
        topic: formValue.topic,
        startDate: formValue.startDate,
        endDate: formValue.endDate,
        skipDuplicates: formValue.skipDuplicates,
        batchSize: formValue.batchSize
      };

      this.importService.startImport(formValue.provider, request)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (job) => {
            this.successMessage = `تم بدء مهمة الاستيراد بنجاح! معرف المهمة: ${job.jobId}`;
            this.hideImportForm();
            this.loadMyJobs();
            this.loading = false;
          },
          error: (error) => {
            console.error('Error starting import:', error);
            this.errorMessage = this.getErrorMessage(error, 'فشل في بدء مهمة الاستيراد. يرجى المحاولة مرة أخرى.');
            this.loading = false;
          }
        });
    } else {
      this.markFormGroupTouched(this.importForm);
    }
  }

  cancelJob(job: ImportJob): void {
    if (confirm(`هل أنت متأكد من إلغاء مهمة الاستيراد؟`)) {
      this.importService.cancelImportJob(job.jobId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedJob) => {
            this.successMessage = 'تم إلغاء مهمة الاستيراد بنجاح!';
            this.loadMyJobs();
            if (this.isAdmin()) {
              this.loadAllJobs();
            }
          },
          error: (error) => {
            console.error('Error cancelling job:', error);
            this.errorMessage = 'فشل في إلغاء مهمة الاستيراد. يرجى المحاولة مرة أخرى.';
          }
        });
    }
  }

  retryJob(job: ImportJob): void {
    if (confirm(`هل تريد إعادة محاولة مهمة الاستيراد؟`)) {
      this.importService.retryImportJob(job.jobId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedJob) => {
            this.successMessage = 'تم إعادة محاولة مهمة الاستيراد بنجاح!';
            this.loadMyJobs();
            if (this.isAdmin()) {
              this.loadAllJobs();
            }
          },
          error: (error) => {
            console.error('Error retrying job:', error);
            this.errorMessage = 'فشل في إعادة محاولة مهمة الاستيراد. يرجى المحاولة مرة أخرى.';
          }
        });
    }
  }

  onFilterChange(filter: 'my' | 'all' | 'active' | 'completed' | 'failed'): void {
    this.currentFilter = filter;
    this.selectedStatus = null;
    
    switch (filter) {
      case 'my':
        this.loadMyJobs();
        break;
      case 'all':
        this.loadAllJobs();
        break;
      case 'active':
        this.selectedStatus = ImportJobStatus.IN_PROGRESS;
        this.loadJobsByStatus(ImportJobStatus.IN_PROGRESS);
        break;
      case 'completed':
        this.selectedStatus = ImportJobStatus.COMPLETED;
        this.loadJobsByStatus(ImportJobStatus.COMPLETED);
        break;
      case 'failed':
        this.selectedStatus = ImportJobStatus.FAILED;
        this.loadJobsByStatus(ImportJobStatus.FAILED);
        break;
    }
  }

  private loadJobsByStatus(status: ImportJobStatus): void {
    if (!this.isAdmin()) return;
    
    this.loadingJobs = true;
    this.importService.getImportJobsByStatus(status)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (jobs) => {
          this.allJobs = jobs;
          this.loadingJobs = false;
        },
        error: (error) => {
          console.error('Error loading jobs by status:', error);
          this.errorMessage = 'فشل في تحميل مهام الاستيراد. يرجى المحاولة مرة أخرى.';
          this.loadingJobs = false;
        }
      });
  }

  getCurrentJobs(): ImportJob[] {
    return this.currentFilter === 'my' ? this.myJobs : this.allJobs;
  }

  getProviderDisplayName(provider: string): string {
    const providerNames: { [key: string]: string } = {
      'youtube': 'YouTube',
      'vimeo': 'Vimeo',
      'mock': 'Mock Provider'
    };
    return providerNames[provider] || provider;
  }

  getProvidersCount(): number {
    return this.statistics ? Object.keys(this.statistics.jobsByProvider).length : 0;
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  private getErrorMessage(error: any, defaultMessage: string): string {
    if (error?.error?.message) {
      return error.error.message;
    } else if (error?.message) {
      return error.message;
    } else if (error?.status === 401) {
      return 'غير مصرح لك بالوصول. يرجى تسجيل الدخول مرة أخرى.';
    } else if (error?.status === 403) {
      return 'ليس لديك صلاحيات كافية لتنفيذ هذا الإجراء.';
    } else if (error?.status === 404) {
      return 'مزود المحتوى غير موجود.';
    } else if (error?.status === 503) {
      return 'مزود المحتوى غير متاح حالياً.';
    } else if (error?.status >= 500) {
      return 'خطأ في الخادم. يرجى المحاولة لاحقاً.';
    }
    return defaultMessage;
  }
} 