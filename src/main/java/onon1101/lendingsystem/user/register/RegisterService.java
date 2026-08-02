package onon1101.lendingsystem.user.register;

import java.util.Locale;
import java.util.UUID;
import onon1101.lendingsystem.sharedkernel.EmailUtil;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RegisterService {

    private static final DomainError INVALID_REGISTRATION =
            new DomainError("User.InvalidRegistration", "The User cannot be registered.");
    private static final DomainError INVALID_EMAIL =
            new DomainError("User.InvalidEmail", "The Invalid Email Address.");

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

    @Transactional()
    public Result<RegisterResult> register(String username, String password, String email) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        try {
            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (!EmailUtil.validateEmail(normalizedEmail)) {
                auditLogger.registerFailed(normalizedUsername, "invalid_email");
                return Result.failure(INVALID_EMAIL);
            }

            String passwordEncoded = passwordEncoder.encode(password);

            // todo: 需要寄信驗證

            RegisterAccount account =
                    accountWriter
                            .registerAccount(normalizedUsername, passwordEncoded, normalizedEmail)
                            .orElse(null);

            if (account == null) {
                auditLogger.registerFailed(normalizedUsername, "account_conflict");
                return Result.failure(INVALID_REGISTRATION);
            }

            auditSuccessAfterCommit(normalizedUsername, account.publicUserId());
            return Result.success(new RegisterResult(account.publicUserId()));
        } catch (RuntimeException exception) {
            auditLogger.registerFailed(normalizedUsername, "system_error");
            throw exception;
        }
    }

    /// Commit 之後，才做 logger 紀錄
    /// @param normalizedUsername 使用者名稱
    /// @param publicUserId 公開使用者 UUID
    private void auditSuccessAfterCommit(String normalizedUsername, UUID publicUserId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            auditLogger.registerSuccess(normalizedUsername, publicUserId);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        auditLogger.registerSuccess(normalizedUsername, publicUserId);
                    }
                });
    }
}
