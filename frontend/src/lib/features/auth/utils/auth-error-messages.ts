const messages: Record<string, string> = {
	'Auth.InvalidCredentials': '帳號或密碼不正確，請重新輸入。',
	'Auth.TooManyAttempts': '登入失敗次數過多，請稍後再試。',
	'ResetPassword.InvalidResetToken': '重設密碼連結無效或已過期。'
};

export function getAuthErrorMessage(errorCode: string | null): string {
	return (errorCode && messages[errorCode]) || '操作失敗，請稍後再試。';
}
