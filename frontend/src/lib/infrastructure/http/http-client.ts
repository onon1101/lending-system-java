// src/lib/infrastructure/http/http-client.ts

import type { ApiResponse } from "./api-response";
import { ApiContractError, ApiError } from "./api-error";

export type RequestOptions<TBody> = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: TBody;
  headers?: HeadersInit;
  signal?: AbortSignal;
};

async function send<TResponse, TBody>(
  url: string,
  options: RequestOptions<TBody>,
): Promise<ApiResponse<TResponse>> {
  const headers = new Headers(options.headers);

  if (!headers.has("Accept")) {
    headers.set("Accept", "application/json");
  }

  const isFormData =
    typeof FormData !== "undefined" && options.body instanceof FormData;

  if (isFormData) {
    headers.delete("Content-Type");
  } else if (options.body !== undefined && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(url, {
    method: options.method ?? "GET",
    headers,
    body:
      options.body === undefined
        ? undefined
        : isFormData
          ? (options.body as FormData)
          : JSON.stringify(options.body),
    signal: options.signal,
  });

  let raw: unknown;

  try {
    raw = await response.json();
  } catch {
    if (!response.ok) {
      throw new ApiError(response.status, null);
    }

    throw new ApiContractError("API response is null valid JSON");
  }

  const result =
    typeof raw === "object" && raw !== null
      ? (raw as Partial<ApiResponse<TResponse>>)
      : null;

  if (!response.ok) {
    throw new ApiError(
      response.status,
      typeof result?.errorCode === "string" ? result.errorCode : null,
    );
  }

  if (!result || typeof result.isSuccess !== "boolean" || !("data" in result)) {
    throw new ApiContractError();
  }

  if (!result.isSuccess) {
    throw new ApiError(
      response.status,
      typeof result.errorCode === "string" ? result.errorCode : null,
    );
  }

  return result as ApiResponse<TResponse>;
}

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
  const result = await send<TResponse, TBody>(url, options);

  if (result.data === null || result.data === undefined) {
    throw new ApiContractError("Successful API response contains null data");
  }

  return result.data;
}

export async function requestVoid<TBody = never>(
  url: string,
  options: RequestOptions<TBody> = {},
): Promise<void> {
  await send<null, TBody>(url, options);
}
