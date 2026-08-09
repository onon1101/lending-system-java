import { requestVoid } from '$lib/infrastructure/http/http-client';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { LogoutRequest } from '../types/auth.types';

export function logout(body: LogoutRequest): Promise<void> {
	return requestVoid<LogoutRequest>(API_ROUTES.auth.logout, { method: 'POST', body });
}
