// src/lib/infrastructure/http/api-error.ts

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly errorCode: string | null,
    message = "API request failed",
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export class ApiContractError extends Error {
  constructor(message = "API response does not match the expected contract") {
    super(message);
    this.name = "ApiContractError";
  }
}
