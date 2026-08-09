import { request } from "$lib/infrastructure/http";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import type {
  RefreshTokenRequest,
  RefreshTokenResponse,
} from "../types/auth.types";

export function refreshToken(
  body: RefreshTokenRequest,
): Promise<RefreshTokenResponse> {
  return request<RefreshTokenResponse, RefreshTokenRequest>(
    API_ROUTES.auth.refresh,
    {
      method: "POST",
      body,
    },
  );
}
