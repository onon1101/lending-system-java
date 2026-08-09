import { request } from '$lib/infrastructure/http/http-client';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { MessageResponse, ResetPasswordRequest } from '../types/auth.types';

export function resetPassword(body: ResetPasswordRequest): Promise<MessageResponse> {
	return request<MessageResponse, ResetPasswordRequest>(API_ROUTES.auth.resetPassword, {
		method: 'POST',
		body
	});
}
