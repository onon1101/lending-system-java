const registerErrorMessage: Record<string, string> = {
  "User.InvalidEmail": "Email 格式不正確, 請重新確認。",
  "User.InvalidRegistration": "帳號資料無法使用，請修改後再是一次。",
};

export function getRegisterErrorMessage(errorCode: string | null): string {
  if (!errorCode) {
    return "註冊失敗，請稍後再試。";
  }

  return registerErrorMessage[errorCode] ?? "註冊失敗，請稍後再試。";
}
