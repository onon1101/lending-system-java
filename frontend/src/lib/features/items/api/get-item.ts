import { authenticatedRequest } from '$lib/infrastructure/http/authenticated-fetch';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { ItemDetail } from '../types/item.types';

export function getItem(itemId: string): Promise<ItemDetail> {
	return authenticatedRequest<ItemDetail>(API_ROUTES.items.retrieve(itemId));
}
