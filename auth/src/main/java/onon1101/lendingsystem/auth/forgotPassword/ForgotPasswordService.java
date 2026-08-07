package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Locale;
import onon1101.lendingsystem.auth.commons.PasswordTokenService;
import onon1101.lendingsystem.auth.forgotPassword.audit.ForgotPasswordAuditPolicy;
import onon1101.lendingsystem.auth.forgotPassword.email.PasswordResetEmailRequested;
import onon1101.lendingsystem.auth.forgotPassword.error.InvalidEmailDomainError;
import onon1101.lendingsystem.sharedkernel.EmailUtil;
import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForgotPasswordService {

    private final ForgotPasswordAccountReader accountReader;
    private final PasswordTokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    public ForgotPasswordService(
            ForgotPasswordAccountReader accountReader,
            PasswordTokenService tokenService,
            ApplicationEventPublisher eventPublisher) {
        this.accountReader = accountReader;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    @AuditedCommand(ForgotPasswordAuditPolicy.class)
    public Result<ForgotPasswordResult> handle(ForgotPasswordCommand command) {
        String email = command.email();

        if (email.isBlank()) {
            return Result.failure(new InvalidEmailDomainError());
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (!EmailUtil.validateEmail(normalizedEmail)) {
            return Result.failure(new InvalidEmailDomainError());
        }

        // read account
        ForgotPasswordAccount account = accountReader.findByEmail(normalizedEmail).orElse(null);

        if (account == null) {
            // 不管帳號存在與否，都成功
            return Result.success(new ForgotPasswordResult(null));
        }

        String token = tokenService.createToken(account.publicUserId(), account.username());

        eventPublisher.publishEvent(
                new PasswordResetEmailRequested(normalizedEmail, account.username(), token));

        // return
        return Result.success(new ForgotPasswordResult(account.publicUserId()));
    }
}
