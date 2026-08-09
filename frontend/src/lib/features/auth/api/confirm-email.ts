import { request } from '$lib/infrastructure/http/http-client';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { ConfirmEmailRequest, MessageResponse } from '../types/auth.types';

export function confirmEmail(body: ConfirmEmailRequest): Promise<MessageResponse> {
	return request<MessageResponse, ConfirmEmailRequest>(API_ROUTES.auth.confirmEmail, {
		method: 'POST',
		body
	});
}
