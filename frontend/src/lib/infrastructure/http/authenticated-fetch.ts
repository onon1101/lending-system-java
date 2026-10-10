import { browser } from "$app/environment";
import { goto } from "$app/navigation";
import { authState } from "$lib/features/auth/state/auth-state.svelte";
import type {
  RefreshTokenRequest,
  RefreshTokenResponse,
} from "$lib/features/auth/types/auth.types";
import {
  clearTokens,
  getAccessToken,
  getRefreshToken,
  storeTokens,
} from "$lib/infrastructure/storage/token-storage";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import { ApiContractError, ApiError } from "./api-error";
import { request, requestVoid, type RequestOptions } from "./http-client";

let refreshTask: {
  token: string;
  promise: Promise<void>;
} | null = null;

function isUnauthorized(error: unknown): error is ApiError {
  return error instanceof ApiError && error.status === 401;
}

function expireSession(expectedRefreshToken: string | null): void {
  if (getRefreshToken() !== expectedRefreshToken) {
    return;
  }

  clearTokens();
  authState.clear();

  if (browser) {
    void goto("/login").catch(() => {
      window.location.assign("/login");
    });
  }
}

function authorize<TBody>(
  options: RequestOptions<TBody>,
  token: string | null,
): RequestOptions<TBody> {
  const headers = new Headers(options.headers);

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  } else {
    headers.delete("Authorization");
  }

  return { ...options, headers };
}

async function performRefresh(token: string): Promise<void> {
  try {
    // 使用一般 request，避免 refresh 本身再觸發 refresh。
    const result = await request<RefreshTokenResponse, RefreshTokenRequest>(
      API_ROUTES.auth.refresh,
      {
        method: "POST",
        body: { refreshToken: token },
      },
    );

    if (
      typeof result.accessToken !== "string" ||
      !result.accessToken ||
      typeof result.refreshToken !== "string" ||
      !result.refreshToken ||
      result.tokenType !== "Bearer"
    ) {
      throw new ApiContractError("Invalid refresh token response");
    }

    // 登出或重新登入後，不覆寫目前的 token。
    if (getRefreshToken() !== token) {
      throw new ApiError(401, null, "Session changed during refresh");
    }

    storeTokens(result);
  } catch (error) {
    if (
      error instanceof ApiError &&
      (error.status === 401 || error.errorCode === "Auth.InvalidRefreshToken")
    ) {
      expireSession(token);
    }

    throw error;
  }
}

function refreshAccessToken(): Promise<void> {
  const token = getRefreshToken();

  if (!browser || !token) {
    expireSession(token);
    return Promise.reject(new ApiError(401, null, "Refresh token is missing"));
  }

  if (refreshTask?.token === token) {
    return refreshTask.promise;
  }

  const promise = performRefresh(token).finally(() => {
    if (refreshTask?.promise === promise) {
      refreshTask = null;
    }
  });

  refreshTask = { token, promise };
  return promise;
}

async function withRefresh<T>(
  send: (token: string | null) => Promise<T>,
  signal?: AbortSignal,
): Promise<T> {
  signal?.throwIfAborted();

  const originalAccessToken = getAccessToken();

  try {
    return await send(originalAccessToken);
  } catch (error) {
    if (!isUnauthorized(error)) {
      throw error;
    }

    signal?.throwIfAborted();

    // 其他請求可能已完成 refresh，直接使用更新後的 token。
    const currentAccessToken = getAccessToken();

    if (!currentAccessToken || currentAccessToken === originalAccessToken) {
      await refreshAccessToken();
    }

    signal?.throwIfAborted();

    const retryAccessToken = getAccessToken();
    const retryRefreshToken = getRefreshToken();

    try {
      // 最多重試一次，避免無限 refresh。
      return await send(retryAccessToken);
    } catch (retryError) {
      if (isUnauthorized(retryError) && getAccessToken() === retryAccessToken) {
        expireSession(retryRefreshToken);
      }

      throw retryError;
    }
  }
}

export function authenticatedRequest<TResponse, TBody = never>(
  url: string,
  options: RequestOptions<TBody> = {},
): Promise<TResponse> {
  return withRefresh(
    (token) => request<TResponse, TBody>(url, authorize(options, token)),
    options.signal,
  );
}

export function authenticatedRequestVoid<TBody = never>(
  url: string,
  options: RequestOptions<TBody> = {},
): Promise<void> {
  return withRefresh(
    (token) => requestVoid<TBody>(url, authorize(options, token)),
    options.signal,
  );
}
