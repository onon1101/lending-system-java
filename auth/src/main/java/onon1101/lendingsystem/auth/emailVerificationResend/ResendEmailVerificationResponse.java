package onon1101.lendingsystem.auth.emailVerificationResend;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResendEmailVerificationResponse(
        @Schema(example = "若此 Email 尚未完成驗證，系統將寄送驗證信") String message) {}
