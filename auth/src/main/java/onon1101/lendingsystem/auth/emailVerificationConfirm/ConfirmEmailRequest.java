package onon1101.lendingsystem.auth.emailVerificationConfirm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Email 驗證確認請求")
public record ConfirmEmailRequest(
        @Schema(
                        description = "Email 驗證 token",
                        example = "email-token",
                        accessMode = Schema.AccessMode.WRITE_ONLY)
                @NotBlank
                String token) {}
