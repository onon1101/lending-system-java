package onon1101.lendingsystem.auth.refreshToken;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Refresh token 回應")
public record RefreshTokenResponse(
        @Schema(description = "新的 JWT access token")
        String accessToken,

        @Schema(description = "Access token 類型")
        String tokenType,

        @Schema(description = "Access token 有效秒數")
        long expiresIn,

        @Schema(description = "選轉後的新 refresh token")
        String refreshToken,

        @Schema(description = "refresh token 有效秒數")
        long refreshTokenExpires) {
}
