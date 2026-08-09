import { request } from '$lib/infrastructure/http/http-client';
import { API_ROUTES } from '$lib/shared/constants/api-routes';
import type { RegisterUserRequest, RegisterUserResponse } from '../types/user.types';

export function registerUser(body: RegisterUserRequest): Promise<RegisterUserResponse> {
	return request<RegisterUserResponse, RegisterUserRequest>(API_ROUTES.users.register, {
		method: 'POST',
		body
	});
}
