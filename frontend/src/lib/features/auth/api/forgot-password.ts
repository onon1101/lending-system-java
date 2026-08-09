import { request } from '$lib/infrastructure/http/http-client';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { ForgotPasswordRequest, MessageResponse } from '../types/auth.types';

export function forgotPassword(body: ForgotPasswordRequest): Promise<MessageResponse> {
	return request<MessageResponse, ForgotPasswordRequest>(API_ROUTES.auth.forgotPassword, {
		method: 'POST',
		body
	});
}
