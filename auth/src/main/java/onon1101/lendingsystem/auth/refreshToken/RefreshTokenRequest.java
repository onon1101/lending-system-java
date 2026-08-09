package onon1101.lendingsystem.auth.refreshToken;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must not be blank.")
        String refreshToken
) {
}
