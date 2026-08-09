// src/lib/infrastructure/http/http-client.ts

import type { ApiResponse } from "./api-response";
import { ApiContractError, ApiError } from "./api-error";

export type RequestOptions<TBody> = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: TBody;
  headers?: HeadersInit;
  signal?: AbortSignal;
};

/**
 *
 * @param url website url
 * @param options requestOptions
 * @returns
 */
export async function request<TResponse, TBody = never>(
  url: string,
  options: RequestOptions<TBody> = {},
): Promise<TResponse> {
  const response = await fetch(url, {
    method: options.method ?? "GET",
    headers: {
      Accept: "application/json",
      ...(options.body === undefined
        ? {}
        : { "Content-Type": "application/json" }),
      ...options.headers,
    },
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
    signal: options.signal,
  });

  let result: ApiResponse<TResponse>;

  try {
    result = (await response.json()) as ApiResponse<TResponse>;
  } catch {
    throw new ApiContractError("API response is not valid JSON");
  }

  if (typeof result.isSuccess !== "boolean") {
    throw new ApiContractError();
  }

  if (!response.ok || !result.isSuccess) {
    throw new ApiError(response.status, result.errorCode);
  }

  if (result.data === null) {
    throw new ApiContractError("Successful API response contains null data");
  }

  return result.data;
}

export async function requestVoid<TBody = never>(
  url: string,
  options: RequestOptions<TBody> = {},
): Promise<void> {
  const response = await fetch(url, {
    method: options.method ?? "GET",
    headers: {
      Accept: "application/json",
      ...(options.body === undefined
        ? {}
        : { "Content-Type": "application/json" }),
      ...options.headers,
    },
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
    signal: options.signal,
  });

  let result: ApiResponse<null>;

  try {
    result = (await response.json()) as ApiResponse<null>;
  } catch {
    throw new ApiContractError("API response is not valid JSON");
  }

  if (!response.ok || !result.isSuccess) {
    throw new ApiError(response.status, result.errorCode);
  }
}
