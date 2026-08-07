package onon1101.lendingsystem.user.register;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "註冊請求")
public record RegisterRequest(
        @Schema(description = "使用者帳號", example = "member001", maxLength = 50)
                @NotBlank
                @Size(max = 50)
                String username,
        @Schema(
                        description = "使用者密碼",
                        example = "correct-password",
                        maxLength = 100,
                        accessMode = Schema.AccessMode.WRITE_ONLY)
                @NotBlank
                @Size(max = 100)
                String password,
        @Schema(description = "註冊 Email", example = "member001@example.com", maxLength = 255)
                @NotBlank
                @Size(max = 255)
                String email) {}
