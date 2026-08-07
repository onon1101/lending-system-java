package onon1101.lendingsystem.auth.emailVerificationConfirm;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Email 驗證回應")
public record ValidateEmailResponse(
        @Schema(description = "處理結果訊息", example = "已驗證電子郵件") String message) {}
