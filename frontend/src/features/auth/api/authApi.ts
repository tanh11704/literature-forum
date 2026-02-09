import { api } from "@/lib/axios";
import type { AuthResponse, LoginRequest } from "../types"; // 👇 Import từ file types vừa tạo

export const loginWithEmail = async (
  data: LoginRequest,
): Promise<AuthResponse> => {
  const response = await api.post("/auth/login", data);
  return response.data;
};
