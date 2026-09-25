package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Locale;
import onon1101.lendingsystem.auth.commons.PasswordTokenService;
import onon1101.lendingsystem.auth.forgotPassword.audit.ForgotPasswordAuditPolicy;
import onon1101.lendingsystem.auth.forgotPassword.email.PasswordResetEmailRequested;
import onon1101.lendingsystem.auth.forgotPassword.error.InvalidEmailDomainError;
import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.email.EmailUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import reactor.core.publisher.Mono;

@Service
public class ForgotPasswordService {

    private final ForgotPasswordAccountReader accountReader;
    private final PasswordTokenService tokenService;
    private final TransactionalEventPublisher eventPublisher;

    public ForgotPasswordService(
            ForgotPasswordAccountReader accountReader,
            PasswordTokenService tokenService,
            TransactionalEventPublisher eventPublisher) {
        this.accountReader = accountReader;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    @AuditedCommand(ForgotPasswordAuditPolicy.class)
    public Mono<Result<ForgotPasswordResult>> handle(ForgotPasswordCommand command) {
        String email = command.email();

        if (email.isBlank()) {
            return Mono.just(Result.failure(new InvalidEmailDomainError()));
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (EmailUtil.validateEmail(normalizedEmail)) {
            return Mono.just(Result.failure(new InvalidEmailDomainError()));
        }

        return accountReader
                .findByEmail(normalizedEmail)
                .flatMap(
                        account -> {
                            String token =
                                    tokenService.createToken(
                                            account.publicUserId(), account.username());
                            return eventPublisher
                                    .publishEvent(
                                            new PasswordResetEmailRequested(
                                                    normalizedEmail, account.username(), token))
                                    .thenReturn(
                                            Result.success(
                                                    new ForgotPasswordResult(
                                                            account.publicUserId())));
                        })
                .switchIfEmpty(
                        Mono.fromSupplier(() -> Result.success(new ForgotPasswordResult(null))));
    }
}
