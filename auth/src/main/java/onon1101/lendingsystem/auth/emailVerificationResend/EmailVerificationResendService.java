package onon1101.lendingsystem.auth.emailVerificationResend;

import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendRequested;
import onon1101.lendingsystem.auth.emailVerificationResend.redis.EmailVerificationResendThrottle;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.email.EmailUtil;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import reactor.core.publisher.Mono;

@Service
public class EmailVerificationResendService {

    private final EmailVerificationAccountReader accountReader;
    private final EmailVerificationResendThrottle throttle;
    private final EmailValidateTokenService tokenService;
    private final TransactionalEventPublisher eventPublisher;

    public EmailVerificationResendService(
            EmailVerificationAccountReader accountReader,
            EmailVerificationResendThrottle throttle,
            EmailValidateTokenService tokenService,
            TransactionalEventPublisher eventPublisher) {
        this.accountReader = accountReader;
        this.throttle = throttle;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Mono<Result<ResendEmailVerificationResult>> resend(
            ResendEmailVerificationCommand command) {
        if (EmailUtil.validateEmail(command.email())) {
            return Mono.just(genericSuccess());
        }

        return accountReader
                .findPendingByEmail(command.email())
                .flatMap(
                        account ->
                                throttle.acquire(account.publicUserId())
                                        .flatMap(
                                                acquired ->
                                                        acquired
                                                                ? publish(account)
                                                                : Mono.just(genericSuccess())))
                .switchIfEmpty(Mono.fromSupplier(this::genericSuccess));
    }

    private Mono<Result<ResendEmailVerificationResult>> publish(EmailVerificationAccount account) {
        String token = tokenService.createToken(account.publicUserId(), account.username());
        return eventPublisher
                .publishEvent(
                        new EmailVerificationResendRequested(
                                account.email(), account.username(), token))
                .thenReturn(genericSuccess());
    }

    private Result<ResendEmailVerificationResult> genericSuccess() {
        return Result.success(new ResendEmailVerificationResult());
    }
}
