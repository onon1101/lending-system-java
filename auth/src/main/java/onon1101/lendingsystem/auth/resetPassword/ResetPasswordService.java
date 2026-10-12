package onon1101.lendingsystem.auth.resetPassword;

import onon1101.lendingsystem.auth.commons.PasswordTokenService;
import onon1101.lendingsystem.auth.resetPassword.error.InvalidResetTokenDomainError;
import onon1101.lendingsystem.infrastructure.security.token.TokenPayload;
import onon1101.lendingsystem.shared.domain.Result;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.security.oauth2.jwt.BadJwtException;

@Service
public class ResetPasswordService {

    private final PasswordTokenService tokenService;
    private final ResetPasswordWriter passwordWriter;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(
            PasswordTokenService tokenService,
            ResetPasswordWriter passwordWriter,
            PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.passwordWriter = passwordWriter;
        this.tokenService = tokenService;
    }

    // todo: 透過 user_public_id 查詢 redis
    @Transactional
    public Mono<Result<ResetPasswordResult>> execute(
            ResetPasswordCommand command) {

        return decodeResetToken(command.resetToken())
                .flatMap(payload -> Mono
                        .fromCallable(() ->
                                passwordEncoder.encode(command.newPassword()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(
                                encodedPassword -> passwordWriter.updatePassword(
                                        payload.publicUserId(),
                                        encodedPassword,
                                        payload.issuedAt()
                                ))
                        .map(updated -> updated
                                ? Result.success(new ResetPasswordResult())
                                : Result.<ResetPasswordResult>failure(
                                new InvalidResetTokenDomainError()))
                )
                .switchIfEmpty(Mono.fromSupplier(() -> Result.failure(
                        new InvalidResetTokenDomainError())));
    }

    private Mono<TokenPayload> decodeResetToken(String token) {
        return Mono
                .fromCallable(() -> tokenService.decode(token))
                .onErrorResume(BadJwtException.class, error -> Mono.empty())
                .onErrorResume(IllegalArgumentException.class,
                        error -> Mono.empty());
    }
}
