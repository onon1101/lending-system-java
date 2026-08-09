import type { HandleServerError } from '@sveltejs/kit';
export const handleError: HandleServerError = ({ error, status, message }) => {
	console.error('Server error', { error, status, message });
	return { message: '伺服器發生未預期錯誤' };
};
