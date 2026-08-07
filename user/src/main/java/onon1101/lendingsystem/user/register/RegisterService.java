package onon1101.lendingsystem.user.register;

import onon1101.lendingsystem.configurations.email.EmailUtil;
import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.user.register.audit.RegistrationAuditPolicy;
import onon1101.lendingsystem.user.register.email.EmailValidateRequested;
import onon1101.lendingsystem.user.register.error.InvalidEmailDomainError;
import onon1101.lendingsystem.user.register.error.InvalidRegistrationDomainError;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {

    private final RegisterAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailValidateTokenService emailValidateTokenService;

    public RegisterService(
            RegisterAccountWriter accountWriter,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher,
            EmailValidateTokenService emailValidateTokenService) {
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.emailValidateTokenService = emailValidateTokenService;
    }

    @Transactional
    @AuditedCommand(RegistrationAuditPolicy.class)
    public Result<RegisterResult> register(RegisterCommand command) {
        String username = command.username();
        String email = command.email();
        String password = command.password();

        if (EmailUtil.validateEmail(email)) {
            return Result.failure(new InvalidEmailDomainError());
        }

        String passwordEncoded = passwordEncoder.encode(password);

        RegisterAccount account =
                accountWriter.registerAccount(username, passwordEncoded, email).orElse(null);

        if (account == null) {
            return Result.failure(new InvalidRegistrationDomainError());
        }

        // 驗證 Email 是否有效 Token
        String emailValidateToken =
                emailValidateTokenService.createToken(account.publicUserId(), username);
        eventPublisher.publishEvent(
                new EmailValidateRequested(email, username, emailValidateToken));

        return Result.success(new RegisterResult(account.publicUserId()));
    }
}
