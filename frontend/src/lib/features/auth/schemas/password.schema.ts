export const PASSWORD_MIN_LENGTH = 12;
export const PASSWORD_MAX_LENGTH = 128;

export function validateNewPassword(password: string): string | null {
	if (password.length < PASSWORD_MIN_LENGTH || password.length > PASSWORD_MAX_LENGTH) {
		return `密碼長度必須介於 ${PASSWORD_MIN_LENGTH} 到 ${PASSWORD_MAX_LENGTH} 個字元。`;
	}
	return null;
}
