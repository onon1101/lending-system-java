import { getAccessToken } from '$lib/infrastructure/storage/token-storage';
import { request, requestVoid, type RequestOptions } from './http-client';

function authorize<TBody>(options: RequestOptions<TBody>): RequestOptions<TBody> {
	const token = getAccessToken();
	return {
		...options,
		headers: {
			...options.headers,
			...(token ? { Authorization: `Bearer ${token}` } : {})
		}
	};
}

export function authenticatedRequest<TResponse, TBody = never>(
	url: string,
	options: RequestOptions<TBody> = {}
): Promise<TResponse> {
	return request<TResponse, TBody>(url, authorize(options));
}

export function authenticatedRequestVoid<TBody = never>(
	url: string,
	options: RequestOptions<TBody> = {}
): Promise<void> {
	return requestVoid<TBody>(url, authorize(options));
}
