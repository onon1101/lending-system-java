package onon1101.lendingsystem.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登入回應")
public record LoginResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
                String accessToken,
        @Schema(description = "Token 類型", example = "Bearer") String tokenType,
        @Schema(description = "Token 有效秒數", example = "3600") long expiresIn) {}
