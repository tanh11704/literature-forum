import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { cn } from "@/lib/utils";
import { Loader2, Terminal, AlertTriangle } from "lucide-react";
import { useLogin } from "../api/useLogin";
import type { AxiosError } from "axios";
import type { SpringBootError } from "../types";

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export const LoginForm = () => {
  // 1. Gọi hook mutation
  const { mutate: login, isPending, error: apiError } = useLogin();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginFormValues) => {
    // 2. Gọi API Login
    login(data);
  };

  return (
    <div className="border border-border bg-card p-8 rounded-sm shadow-neon max-w-md w-full relative overflow-hidden group">
      {/* Hiệu ứng quét (Scanline effect) trang trí */}
      <div className="absolute inset-0 bg-gradient-to-b from-transparent via-primary/5 to-transparent -translate-y-full group-hover:animate-[scan_2s_ease-in-out_infinite] pointer-events-none" />

      <div className="flex items-center gap-2 mb-6 text-primary relative z-10">
        <Terminal size={24} />
        <h1 className="text-xl font-mono font-bold tracking-wider">
          ACCESS_CONTROL
        </h1>
      </div>

      {/* Hiển thị lỗi từ Backend (nếu có) */}
      {apiError && (
        <div className="mb-4 p-3 border border-destructive/50 bg-destructive/10 text-destructive text-xs font-mono flex items-center gap-2">
          <AlertTriangle size={14} />
          <span>
            ERROR:{" "}
            {(apiError as AxiosError<SpringBootError>)?.response?.data
              ?.message || "CONNECTION_REFUSED"}
          </span>
        </div>
      )}

      <form
        onSubmit={handleSubmit(onSubmit)}
        className="space-y-4 relative z-10"
      >
        {/* Email */}
        <div className="space-y-2">
          <label className="text-xs font-mono text-muted-foreground uppercase">
            Target ID (Email)
          </label>
          <input
            {...register("email")}
            className={cn(
              "w-full bg-black border border-input p-3 font-mono text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary transition-all placeholder:text-muted-foreground/50",
              errors.email && "border-destructive focus:ring-destructive",
            )}
            placeholder="user@careervault.io"
            autoComplete="email"
          />
          {errors.email && (
            <p className="text-xs text-destructive font-mono">
              &gt; {errors.email.message}
            </p>
          )}
        </div>

        {/* Password */}
        <div className="space-y-2">
          <label className="text-xs font-mono text-muted-foreground uppercase">
            Passcode
          </label>
          <input
            type="password"
            {...register("password")}
            className={cn(
              "w-full bg-black border border-input p-3 font-mono text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary transition-all",
              errors.password && "border-destructive focus:ring-destructive",
            )}
            autoComplete="current-password"
          />
          {errors.password && (
            <p className="text-xs text-destructive font-mono">
              &gt; {errors.password.message}
            </p>
          )}
        </div>

        {/* Submit Button */}
        <button
          type="submit"
          disabled={isPending}
          className="w-full bg-primary text-primary-foreground font-mono font-bold py-3 hover:bg-primary/90 hover:shadow-neon transition-all flex justify-center items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isPending ? (
            <>
              <Loader2 className="animate-spin" size={16} />
              AUTHENTICATING...
            </>
          ) : (
            "[ INITIATE_SESSION ]"
          )}
        </button>
      </form>
    </div>
  );
};
