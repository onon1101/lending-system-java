package onon1101.lendingsystem.auth.emailVerificationResend;

import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendRequested;
import onon1101.lendingsystem.auth.emailVerificationResend.redis.EmailVerificationResendThrottle;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.email.EmailUtil;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationResendService {

    private final EmailVerificationAccountReader accountReader;
    private final EmailVerificationResendThrottle throttle;
    private final EmailValidateTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    public EmailVerificationResendService(
            EmailVerificationAccountReader accountReader,
            EmailVerificationResendThrottle throttle,
            EmailValidateTokenService tokenService,
            ApplicationEventPublisher eventPublisher) {
        this.accountReader = accountReader;
        this.throttle = throttle;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Result<ResendEmailVerificationResult> resend(ResendEmailVerificationCommand command) {
        if (EmailUtil.validateEmail(command.email())) {
            return genericSuccess();
        }

        EmailVerificationAccount account =
                accountReader.findPendingByEmail(command.email()).orElse(null);

        if (account == null || !throttle.acquire(account.publicUserId())) {
            return genericSuccess();
        }

        String token = tokenService.createToken(account.publicUserId(), account.username());

        eventPublisher.publishEvent(
                new EmailVerificationResendRequested(
                        account.email(),
                        account.username(), token));

        return genericSuccess();
    }

    private Result<ResendEmailVerificationResult> genericSuccess() {
        return Result.success(new ResendEmailVerificationResult());
    }
}
