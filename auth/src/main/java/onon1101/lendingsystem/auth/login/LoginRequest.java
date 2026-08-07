package onon1101.lendingsystem.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "登入請求")
public record LoginRequest(
        @Schema(description = "使用者帳號", example = "member001", maxLength = 50)
                @NotBlank @Size(max = 50) String username,
        @Schema(
                        description = "使用者密碼",
                        example = "correct-password",
                        maxLength = 100,
                        accessMode = Schema.AccessMode.WRITE_ONLY)
                @NotBlank @Size(max = 100) String password) {}
