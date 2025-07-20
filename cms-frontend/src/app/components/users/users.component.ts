import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService, User, UserCreateRequest, UserUpdateRequest, UserPageResponse } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Subject, takeUntil, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit, OnDestroy {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  pageSize = 10;
  
  // Form states
  showCreateForm = false;
  showEditForm = false;
  selectedUser: User | null = null;
  
  // Forms
  createForm: FormGroup;
  editForm: FormGroup;
  searchForm: FormGroup;
  
  // Filter states
  currentFilter: 'all' | 'admin' | 'editor' | 'active' = 'all';
  searchQuery = '';
  
  // Error handling
  errorMessage = '';
  successMessage = '';

  // Auto refresh
  private destroy$ = new Subject<void>();
  private autoRefreshInterval: any;

  // Statistics
  userStats = {
    total: 0,
    admins: 0,
    editors: 0,
    active: 0,
    inactive: 0
  };

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.createForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['EDITOR', Validators.required]
    });

    this.editForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required],
      isActive: [true]
    });

    this.searchForm = this.fb.group({
      searchQuery: ['']
    });

    // Setup search debounce for client-side filtering
    this.searchForm.get('searchQuery')?.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(value => {
        this.searchQuery = value || '';
        this.applyFilters();
      });
  }

  ngOnInit(): void {
    if (!this.authService.hasRole('ADMIN')) {
      this.errorMessage = 'تم رفض الوصول. تحتاج إلى صلاحيات المدير.';
      return;
    }
    
    this.loadUsers();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.autoRefreshInterval) {
      clearInterval(this.autoRefreshInterval);
    }
  }

  private setupAutoRefresh(): void {
    // Auto refresh users list every 30 seconds
    this.autoRefreshInterval = setInterval(() => {
      if (this.isAdmin() && !this.loading && !this.showCreateForm && !this.showEditForm) {
        this.loadUsers();
      }
    }, 30000);
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    let request: any;

    switch (this.currentFilter) {
      case 'admin':
        request = this.userService.getUsersByRole('ADMIN', this.currentPage, this.pageSize);
        break;
      case 'editor':
        request = this.userService.getUsersByRole('EDITOR', this.currentPage, this.pageSize);
        break;
      case 'active':
        request = this.userService.getActiveUsers(this.currentPage, this.pageSize);
        break;
      default:
        request = this.userService.getAllUsers(this.currentPage, this.pageSize);
    }

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: UserPageResponse) => {
        this.users = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.loading = false;
        this.applyFilters();
        this.calculateStats();
      },
      error: (error: any) => {
        console.error('Error loading users:', error);
        this.errorMessage = this.getErrorMessage(error, 'فشل في تحميل المستخدمين. يرجى المحاولة مرة أخرى.');
        this.loading = false;
      }
    });
  }

  private applyFilters(): void {
    let filtered = [...this.users];

    // Apply search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase().trim();
      filtered = filtered.filter(user => 
        user.email.toLowerCase().includes(query) ||
        user.role.toLowerCase().includes(query) ||
        (user.isActive ? 'نشط' : 'غير نشط').includes(query)
      );
    }

    this.filteredUsers = filtered;
  }

  private calculateStats(): void {
    this.userStats = {
      total: this.totalElements,
      admins: this.users.filter(u => u.role === 'ADMIN').length,
      editors: this.users.filter(u => u.role === 'EDITOR').length,
      active: this.users.filter(u => u.isActive).length,
      inactive: this.users.filter(u => !u.isActive).length
    };
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadUsers();
    }
  }

  onFilterChange(filter: 'all' | 'admin' | 'editor' | 'active'): void {
    this.currentFilter = filter;
    this.currentPage = 0;
    this.searchQuery = '';
    this.searchForm.reset();
    this.loadUsers();
  }

  clearSearch(): void {
    this.searchForm.reset();
    this.searchQuery = '';
    this.applyFilters();
  }

  showCreateUserForm(): void {
    this.showCreateForm = true;
    this.showEditForm = false;
    this.createForm.reset({ role: 'EDITOR' });
    this.clearMessages();
  }

  showEditUserForm(user: User): void {
    this.selectedUser = user;
    this.showEditForm = true;
    this.showCreateForm = false;
    
    this.editForm.patchValue({
      email: user.email,
      role: user.role,
      isActive: user.isActive
    });
    
    this.clearMessages();
  }

  hideForms(): void {
    this.showCreateForm = false;
    this.showEditForm = false;
    this.selectedUser = null;
    this.clearMessages();
  }

  createUser(): void {
    if (this.createForm.valid) {
      this.loading = true;
      const userData: UserCreateRequest = this.createForm.value;
      
      this.userService.createUser(userData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (user) => {
            this.successMessage = `تم إنشاء المستخدم ${user.email} بنجاح!`;
            this.hideForms();
            this.loadUsers();
            this.loading = false;
          },
          error: (error: any) => {
            console.error('Error creating user:', error);
            this.errorMessage = this.getErrorMessage(error, 'فشل في إنشاء المستخدم. يرجى المحاولة مرة أخرى.');
            this.loading = false;
          }
        });
    } else {
      this.markFormGroupTouched(this.createForm);
    }
  }

  updateUser(): void {
    if (this.editForm.valid && this.selectedUser) {
      this.loading = true;
      const userData: UserUpdateRequest = this.editForm.value;
      
      this.userService.updateUser(this.selectedUser.id, userData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (user) => {
            this.successMessage = `تم تحديث المستخدم ${user.email} بنجاح!`;
            this.hideForms();
            this.loadUsers();
            this.loading = false;
          },
          error: (error: any) => {
            console.error('Error updating user:', error);
            this.errorMessage = this.getErrorMessage(error, 'فشل في تحديث المستخدم. يرجى المحاولة مرة أخرى.');
            this.loading = false;
          }
        });
    } else {
      this.markFormGroupTouched(this.editForm);
    }
  }

  deleteUser(user: User): void {
    if (confirm(`هل أنت متأكد من إلغاء تفعيل المستخدم ${user.email}؟`)) {
      this.loading = true;
      
      this.userService.deleteUser(user.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.successMessage = `تم إلغاء تفعيل المستخدم ${user.email} بنجاح!`;
            this.loadUsers();
            this.loading = false;
          },
          error: (error: any) => {
            console.error('Error deleting user:', error);
            this.errorMessage = this.getErrorMessage(error, 'فشل في إلغاء تفعيل المستخدم. يرجى المحاولة مرة أخرى.');
            this.loading = false;
          }
        });
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    let startPage = Math.max(0, this.currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxVisiblePages - 1);
    
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('ar-SA', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
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
      return 'المستخدم غير موجود.';
    } else if (error?.status === 409) {
      return 'المستخدم موجود بالفعل.';
    } else if (error?.status >= 500) {
      return 'خطأ في الخادم. يرجى المحاولة لاحقاً.';
    }
    return defaultMessage;
  }

  // Utility methods
  getRoleDisplayName(role: string): string {
    return role === 'ADMIN' ? 'مدير' : 'محرر';
  }

  getStatusDisplayName(isActive: boolean): string {
    return isActive ? 'نشط' : 'غير نشط';
  }

  getStatusBadgeClass(isActive: boolean): string {
    return isActive ? 'bg-success' : 'bg-secondary';
  }

  getRoleBadgeClass(role: string): string {
    return role === 'ADMIN' ? 'bg-danger' : 'bg-info';
  }

  canEditUser(user: User): boolean {
    const currentUser = this.authService.getCurrentUser();
    return !!(currentUser && (
      currentUser.role === 'ADMIN' || 
      (currentUser.role === 'EDITOR' && user.role === 'EDITOR')
    ));
  }

  canDeleteUser(user: User): boolean {
    const currentUser = this.authService.getCurrentUser();
    return !!(currentUser && currentUser.role === 'ADMIN' && user.id !== currentUser.id);
  }
} 