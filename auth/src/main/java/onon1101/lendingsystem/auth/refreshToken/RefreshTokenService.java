package onon1101.lendingsystem.auth.refreshToken;

import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.token.AccessTokenService;
import onon1101.lendingsystem.auth.login.token.RefreshTokenIssuer;

import onon1101.lendingsystem.auth.login.token.RefreshTokenSession;
import onon1101.lendingsystem.configurations.domain.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenIssuer  refreshTokenIssuer;
    private final AccessTokenService  accessTokenService;

    public RefreshTokenService(
            RefreshTokenIssuer refreshTokenIssuer,
            AccessTokenService accessTokenService
    ) {
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.accessTokenService = accessTokenService;
    }

    public Result<RefreshTokenResult> refresh(
            RefreshTokenCommand command
    ) {
        RefreshTokenSession session =
                refreshTokenIssuer.consume(command.refreshToken())
                        .orElse(null);

        if(session == null) {
            return Result.failure(new InvalidCredentialsDomainError());
        }

        String accessToken =
                accessTokenService.createToken(
                        session.privateUserId(),
                        session.publicUserId(),
                        session.username()
                );

        String newRefreshToken =
                refreshTokenIssuer.createToken(
                        session.privateUserId(),
                        session.publicUserId(),
                        session.username()
                );

        return Result.success(
                new RefreshTokenResult(
                        accessToken,
                        accessTokenService.expiresInSeconds(),
                        newRefreshToken,
                        refreshTokenIssuer.expiresInSeconds()
                )
        );
    }
}
