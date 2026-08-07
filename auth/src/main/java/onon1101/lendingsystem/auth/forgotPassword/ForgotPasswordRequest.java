package onon1101.lendingsystem.auth.forgotPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "忘記密碼請求")
public record ForgotPasswordRequest(
        @Schema(description = "註冊 Email", example = "member001@example.com", maxLength = 255)
                @NotBlank @Size(max = 255) String email) {}
