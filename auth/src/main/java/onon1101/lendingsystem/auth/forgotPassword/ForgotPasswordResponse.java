package onon1101.lendingsystem.auth.forgotPassword;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "忘記密碼回應")
public record ForgotPasswordResponse(
        @Schema(description = "處理結果訊息", example = "已發送 Email") String message) {}
