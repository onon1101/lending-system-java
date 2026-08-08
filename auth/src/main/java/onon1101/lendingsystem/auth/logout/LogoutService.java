package onon1101.lendingsystem.auth.logout;

import onon1101.lendingsystem.auth.login.token.RefreshTokenService;

import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final RefreshTokenService refreshTokenService;

    public LogoutService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    public void logout(LogoutCommand command) {
        refreshTokenService.revoke(command.refreshToken());
    }
}
