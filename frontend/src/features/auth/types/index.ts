import type { User } from "@/stores/useAuthStore";

// 1. Auth Response (Dùng chung cho Login, Register, Refresh Token)
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

// 2. Login DTO
export interface LoginRequest {
  email: string;
  password: string;
}

// 3. Register DTO (Chuẩn bị sẵn cho tương lai)
export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface SpringBootError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}
