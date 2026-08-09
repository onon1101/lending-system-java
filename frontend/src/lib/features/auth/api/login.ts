// src/lib/features/auth/api/login.ts

import { request } from "$lib/infrastructure/http/http-client";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import type { LoginRequest, LoginResponse } from "../types/auth.types";

export function login(body: LoginRequest): Promise<LoginResponse> {
  return request<LoginResponse, LoginRequest>(API_ROUTES.auth.login, {
    method: "POST",
    body,
  });
}
