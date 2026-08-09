import { authenticatedRequest } from '$lib/infrastructure/http/authenticated-fetch';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { DeleteItemRequest, ItemMutationResponse } from '../types/item.types';

export function deleteItem(body: DeleteItemRequest): Promise<ItemMutationResponse> {
	return authenticatedRequest<ItemMutationResponse, DeleteItemRequest>(API_ROUTES.items.delete, {
		method: 'POST',
		body
	});
}
