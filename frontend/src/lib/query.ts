import { QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false, // Tắt tự động fetch khi focus lại tab
      retry: 1, // Chỉ thử lại 1 lần nếu lỗi
      staleTime: 5 * 60 * 1000, // Data coi là "mới" trong 5 phút (Client Cache)
    },
  },
});
