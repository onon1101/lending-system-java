import { authenticatedRequest } from "$lib/infrastructure/http";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import type { ListItemsResponse } from "../types/item.types";

export function listItems(
  page = 0,
  size = 20,
  signal?: AbortSignal,
): Promise<ListItemsResponse> {
  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
  });

  return authenticatedRequest<ListItemsResponse>(
    `${API_ROUTES.items.list}?${query}`,
    { signal },
  );
}
