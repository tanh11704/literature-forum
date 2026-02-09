import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface User {
  id: string;
  email: string;
  fullName: string;
  avatarUrl?: string;
  role: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  accessToken: string | null;

  // Actions
  login: (user: User, accessToken: string, refreshToken: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      accessToken: null,

      login: (user, accessToken, refreshToken) => {
        // Lưu vào LocalStorage (cho Axios dùng)
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        // Lưu vào State (cho UI dùng)
        set({ user, isAuthenticated: true, accessToken });
      },

      logout: () => {
        localStorage.clear();
        set({ user: null, isAuthenticated: false, accessToken: null });
      },
    }),
    {
      name: "auth-storage", // Tên key trong LocalStorage
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }), // Chỉ persist user info, không cần persist function
    },
  ),
);
