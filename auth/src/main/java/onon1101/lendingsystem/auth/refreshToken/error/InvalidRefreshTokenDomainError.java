package onon1101.lendingsystem.auth.refreshToken.error;

import onon1101.lendingsystem.configurations.domain.DomainError;

public final class InvalidRefreshTokenDomainError extends DomainError {

    public InvalidRefreshTokenDomainError() {
        super(
                "Auth.InvalidRefreshToken",
                "Refresh token is invalid, expired, or has already been used."
        );
    }
}
