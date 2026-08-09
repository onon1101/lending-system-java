// src/lib/features/auth/types/auth.types.ts

export type LoginRequest = {
  username: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  refreshToken: string;
  refreshTokenExpiresIn: number;
};

export type LogoutRequest = {
  refreshToken: string;
};

export type ForgotPasswordRequest = {
  email: string;
};

export type ResetPasswordRequest = {
  resetToken: string;
  newPassword: string;
};

export type ConfirmEmailRequest = {
  token: string;
};

export type MessageResponse = {
  message: string;
};

export type ResendEmailVerificationRequest = {
  email: string;
};
