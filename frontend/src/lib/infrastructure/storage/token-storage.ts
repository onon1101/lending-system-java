// src/lib/infrastructure/storage/token-storage.ts

import { browser } from "$app/environment";

const ACCESS_TOKEN_KEY = "accessToken";
const TOKEN_TYPE_KEY = "tokenType";
const REFRESH_TOKEN_KEY = "refreshToken";

export type StoredTokens = {
  accessToken: string;
  tokenType: string;
  refreshToken: string;
};

export function storeTokens(tokens: StoredTokens): void {
  if (!browser) {
    return;
  }

  sessionStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken);
  sessionStorage.setItem(TOKEN_TYPE_KEY, tokens.tokenType);
  sessionStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken);
}

export function getAccessToken(): string | null {
  if (!browser) {
    return null;
  }

  return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
  if (!browser) {
    return null;
  }

  return sessionStorage.getItem(REFRESH_TOKEN_KEY);
}

export function clearTokens(): void {
  if (!browser) {
    return;
  }

  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
  sessionStorage.removeItem(TOKEN_TYPE_KEY);
  sessionStorage.removeItem(REFRESH_TOKEN_KEY);
}
