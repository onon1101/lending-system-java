import type { LoginRequest } from '../types/auth.types';

export function validateLogin(input: LoginRequest): string | null {
	if (!input.username.trim() || !input.password) return '請輸入帳號與密碼。';
	if (input.username.trim().length > 50) return '帳號不可超過 50 個字元。';
	if (input.password.length > 100) return '密碼不可超過 100 個字元。';
	return null;
}
