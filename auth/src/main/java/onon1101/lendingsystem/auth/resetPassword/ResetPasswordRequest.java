package onon1101.lendingsystem.auth.resetPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "重設密碼請求")
public record ResetPasswordRequest(
        @Schema(description = "重設密碼 token", example = "reset-token") @NotBlank String resetToken,
        @Schema(
                        description = "新密碼",
                        example = "new-correct-password",
                        minLength = 12,
                        maxLength = 128,
                        accessMode = Schema.AccessMode.WRITE_ONLY)
                @NotBlank @Size(min = 12, max = 128) String newPassword) {}
