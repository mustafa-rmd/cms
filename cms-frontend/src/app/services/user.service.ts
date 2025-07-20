import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'EDITOR';
  isActive: boolean;
  createdDate: string;
  updatedDate: string;
  createdBy: string;
  updatedBy: string;
}

export interface UserCreateRequest {
  email: string;
  password: string;
  role: 'ADMIN' | 'EDITOR';
}

export interface UserUpdateRequest {
  email?: string;
  role?: 'ADMIN' | 'EDITOR';
  isActive?: boolean;
}

export interface UserPageResponse {
  content: User[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // Get all users with pagination
  getAllUsers(page: number = 0, size: number = 10): Observable<UserPageResponse> {
    const params = new HttpParams()
      .set('pageNumber', page.toString())
      .set('pageSize', size.toString());
    
    return this.http.get<UserPageResponse>(this.apiUrl, { params });
  }

  // Get users by role
  getUsersByRole(role: 'ADMIN' | 'EDITOR', page: number = 0, size: number = 10): Observable<UserPageResponse> {
    const params = new HttpParams()
      .set('pageNumber', page.toString())
      .set('pageSize', size.toString());
    
    return this.http.get<UserPageResponse>(`${this.apiUrl}/role/${role}`, { params });
  }

  // Get active users
  getActiveUsers(page: number = 0, size: number = 10): Observable<UserPageResponse> {
    const params = new HttpParams()
      .set('pageNumber', page.toString())
      .set('pageSize', size.toString());
    
    return this.http.get<UserPageResponse>(`${this.apiUrl}/active`, { params });
  }

  // Get user by ID
  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  // Create new user
  createUser(userData: UserCreateRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, userData);
  }

  // Update user
  updateUser(id: string, userData: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, userData);
  }

  // Delete user (deactivate)
  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
} 