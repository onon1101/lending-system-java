package onon1101.lendingsystem.user.register;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.EmailUtil;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {

    private static final DomainError INVALID_REGISTRATION =
            new DomainError("User.InvalidRegistration", "The User cannot be registered.");

    private final RegisterAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;

    private final RegistrationAuditLogger auditLogger;

    public RegisterService(
            RegisterAccountWriter accountWriter,
            PasswordEncoder passwordEncoder,
            RegistrationAuditLogger auditLogger) {
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
        this.auditLogger = auditLogger;
    }

    @Transactional(readOnly = false)
    public Result<RegisterResult> register(String username, String password, String email) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (!EmailUtil.validateEmail(normalizedEmail)) {
            auditLogger.registerFailed(normalizedUsername);
            return Result.failure(INVALID_REGISTRATION);
        }

        String passwordEncoded = passwordEncoder.encode(password);

        // todo: 需要寄信驗證

        RegisterAccount account =
                accountWriter
                        .registerAccount(normalizedUsername, passwordEncoded, normalizedEmail)
                        .orElse(null);

        if (account == null) {
            auditLogger.registerFailed(normalizedUsername);
            return Result.failure(INVALID_REGISTRATION);
        }

        //todo: 轉換流水號成 uuid

        auditLogger.registerSuccess(normalizedUsername);
        return Result.success(new RegisterResult(Long.toString(account.userId())));
    }
}
