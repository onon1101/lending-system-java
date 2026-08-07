package onon1101.lendingsystem.auth.login;

import onon1101.lendingsystem.configurations.services.CommandResult;

public record LoginResult(
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken,
        long refreshTokenExpiresIn
) implements CommandResult {}
