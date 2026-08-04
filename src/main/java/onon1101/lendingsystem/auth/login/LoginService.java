package onon1101.lendingsystem.auth.login;

import java.time.Duration;
import java.time.Instant;
import onon1101.lendingsystem.auth.login.audit.AuthenticationAuditPolicy;
import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.error.TooManyAttemptsDomainError;
import onon1101.lendingsystem.auth.login.token.AccessTokenService;
import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    // todo: 1. 泛化所有 DomainError
    // todo: 2. 泛化稽核日誌
    //    private static final DomainError INVALID_CREDENTIALS =
    //            new DomainError("Auth.InvalidCredentials",
    //                    "Username or password is incorrect.");
    //
    //    private static final DomainError TOO_MANY_ATTEMPTS =
    //            new DomainError("Auth.TooManyAttempts", "This username try too many times.");
    //
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final LoginAccountReader accountReader;
    private final LoginAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService tokenService;

    public LoginService(
            LoginAccountReader accountReader,
            LoginAccountWriter accountWriter,
            PasswordEncoder passwordEncoder,
            AccessTokenService tokenService) {
        this.accountReader = accountReader;
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // TODO Add refresh token support.
    @Transactional()
    @AuditedCommand(AuthenticationAuditPolicy.class)
    public Result<LoginResult> login(LoginCommand command) {
        Instant now = Instant.now();

        String username = command.username();
        String password = command.password();

        LoginAccount account = accountReader.findByUsername(username).orElse(null);

        // 帳號不存在
        if (account == null) {
            return Result.failure(new InvalidCredentialsDomainError());
        }

        // 嘗試太多次
        if (account.lockedUntil() != null && account.lockedUntil().isAfter(Instant.now())) {
            return Result.failure(new TooManyAttemptsDomainError());
        }

        // 輸入密碼錯誤
        if (!passwordEncoder.matches(password, account.passwordHash())) {
            // 失敗次數寫入資料庫
            FailedAttemptResult attempt =
                    accountWriter.recordFailedAttempt(
                            account.passwordId(), MAX_FAILED_ATTEMPTS, now.plus(LOCK_DURATION));

            if (attempt.locked()) {
                // todo: 稽核日誌
                return Result.failure(new TooManyAttemptsDomainError());
            }

            return Result.failure(new InvalidCredentialsDomainError());
        }

        // 清洗更新次數
        accountWriter.resetFailedAttempts(account.passwordId());

        String accessToken = tokenService.createToken(account.publicUserId(), account.username());

        return Result.success(new LoginResult(accessToken, tokenService.expiresInSeconds()));
    }
}
