package onon1101.lendingsystem.auth.emailVerificationResend;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "重送 Email 驗證信請求")
public record ResendEmailVerificationRequest(
        @Schema(
                        description = "註冊時使用的 Email",
                        example = "member001@example.com",
                        maxLength = 255)
                @NotBlank
                @Size(max = 255)
                String email) {}
