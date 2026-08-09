package onon1101.lendingsystem.auth.refreshToken;

import onon1101.lendingsystem.configurations.services.CommandResult;

public record RefreshTokenResult(
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken,
        long refreshTokenExpiresIn
)
implements CommandResult {
}
