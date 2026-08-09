// src/lib/infrastructure/http/api-response.ts

export type ApiResponse<T> = {
  code: number;
  isSuccess: boolean;
  errorCode: string | null;
  data: T | null;
};
