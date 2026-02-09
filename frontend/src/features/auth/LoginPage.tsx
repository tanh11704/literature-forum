import { LoginForm } from "./components/LoginForm";

const LoginPage = () => {
  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-background p-4 relative">
      {/* Background Grid Decoration (Optional) */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#27272a_1px,transparent_1px),linear-gradient(to_bottom,#27272a_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_50%,#000_70%,transparent_100%)] opacity-20 pointer-events-none" />

      {/* Form chính */}
      <LoginForm />

      {/* Footer Text */}
      <div className="absolute bottom-4 text-[10px] text-muted-foreground font-mono opacity-50">
        :: CAREER_VAULT :: SYSTEM_READY :: v1.0.0 ::
      </div>
    </div>
  );
};

export default LoginPage;
