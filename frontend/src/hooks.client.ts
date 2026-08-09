import type { HandleClientError } from '@sveltejs/kit';
export const handleError: HandleClientError = ({ error, status, message }) => {
	console.error('Client error', { error, status, message });
	return { message: '頁面發生未預期錯誤' };
};
