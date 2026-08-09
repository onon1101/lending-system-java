// src/lib/features/auth/index.ts

export { default as LoginForm } from "./components/LoginForm.svelte";
export { confirmEmail } from './api/confirm-email';
export { forgotPassword } from './api/forgot-password';
export { login } from './api/login';
export { logout } from './api/logout';
export { resetPassword } from './api/reset-password';
export type * from './types/auth.types';
