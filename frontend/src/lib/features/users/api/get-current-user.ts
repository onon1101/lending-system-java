import { authenticatedRequest } from '$lib/infrastructure/http/authenticated-fetch';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { CurrentUser } from '../types/user.types';

export function getCurrentUser(): Promise<CurrentUser> {
	return authenticatedRequest<CurrentUser>(API_ROUTES.users.currentUser);
}
