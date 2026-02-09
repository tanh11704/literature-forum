import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "@/stores/useAuthStore";
import { loginWithEmail } from "./authApi";
import { AxiosError } from "axios";
import type { AuthResponse, LoginRequest, SpringBootError } from "../types"; // 👇 Import từ types

export const useLogin = () => {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  return useMutation({
    mutationFn: (data: LoginRequest) => loginWithEmail(data),
    onSuccess: (data: AuthResponse) => {
      login(data.user, data.accessToken, data.refreshToken);
      navigate("/");
    },
    onError: (error: AxiosError<SpringBootError>) => {
      console.error("Login Failed:", error.response?.data || error.message);
    },
  });
};
