import { authenticatedRequest } from "$lib/infrastructure/http/authenticated-fetch";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import type {
  CreateItemRequest,
  CreateItemResponse,
} from "../types/item.types";

export function createItem(
  body: CreateItemRequest,
  idempotencyKey: string,
): Promise<CreateItemResponse> {
  return authenticatedRequest<CreateItemResponse, CreateItemRequest>(
    API_ROUTES.items.create,
    {
      method: "POST",
      headers: {
        "Idempotency-Key": idempotencyKey,
      },
      body,
    },
  );
}
