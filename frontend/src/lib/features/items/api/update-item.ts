import { authenticatedRequest } from '$lib/infrastructure/http/authenticated-fetch';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { ItemMutationResponse, UpdateItemRequest } from '../types/item.types';

export function updateItem(body: UpdateItemRequest): Promise<ItemMutationResponse> {
	return authenticatedRequest<ItemMutationResponse, UpdateItemRequest>(API_ROUTES.items.update, {
		method: 'POST',
		body
	});
}
