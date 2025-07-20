export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

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