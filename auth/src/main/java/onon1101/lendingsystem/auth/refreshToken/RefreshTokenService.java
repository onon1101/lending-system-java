package onon1101.lendingsystem.auth.refreshToken;

import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.token.AccessTokenService;
import onon1101.lendingsystem.auth.login.token.RefreshTokenIssuer;
import onon1101.lendingsystem.auth.login.token.RefreshTokenSession;
import onon1101.lendingsystem.configurations.domain.Result;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RefreshTokenService {

    private final RefreshTokenIssuer refreshTokenIssuer;
    private final AccessTokenService accessTokenService;

    public RefreshTokenService(
            RefreshTokenIssuer refreshTokenIssuer, AccessTokenService accessTokenService) {
        this.refreshTokenIssuer = refreshTokenIssuer;
        this.accessTokenService = accessTokenService;
    }

    public Mono<Result<RefreshTokenResult>> refresh(RefreshTokenCommand command) {
        return refreshTokenIssuer
                .consume(command.refreshToken())
                .flatMap(session -> issue(session))
                .switchIfEmpty(
                        Mono.fromSupplier(
                                () -> Result.failure(new InvalidCredentialsDomainError())));
    }

    private Mono<Result<RefreshTokenResult>> issue(RefreshTokenSession session) {
        String accessToken =
                accessTokenService.createToken(
                        session.privateUserId(), session.publicUserId(), session.username());
        return refreshTokenIssuer
                .createToken(session.privateUserId(), session.publicUserId(), session.username())
                .map(
                        newRefreshToken ->
                                Result.success(
                                        new RefreshTokenResult(
                                                accessToken, accessTokenService.expiresInSeconds(),
                                                newRefreshToken,
                                                        refreshTokenIssuer.expiresInSeconds())));
    }
}
