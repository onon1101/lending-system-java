package onon1101.lendingsystem.auth.logout;

import onon1101.lendingsystem.auth.login.token.RefreshTokenIssuer;

import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final RefreshTokenIssuer refreshTokenIssuer;

    public LogoutService(RefreshTokenIssuer refreshTokenIssuer) {
        this.refreshTokenIssuer = refreshTokenIssuer;
    }

    public void logout(LogoutCommand command) {
        refreshTokenIssuer.revoke(command.refreshToken());
    }
}
