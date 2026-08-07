package onon1101.lendingsystem.auth.login;

import java.time.Duration;
import java.time.Instant;
import onon1101.lendingsystem.auth.login.audit.AuthenticationAuditPolicy;
import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.error.TooManyAttemptsDomainError;
import onon1101.lendingsystem.auth.login.token.AccessTokenService;
import onon1101.lendingsystem.auth.login.token.RefreshTokenService;
import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    // todo: 1. 泛化所有 DomainError
    // todo: 2. 泛化稽核日誌

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final LoginAccountReader accountReader;
    private final LoginAccountWriter accountWriter;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final IClock clock;

    public LoginService(
            LoginAccountReader accountReader,
            LoginAccountWriter accountWriter,
            PasswordEncoder passwordEncoder,
            AccessTokenService tokenService,
            RefreshTokenService refreshTokenService,
            IClock clock) {
        this.accountReader = accountReader;
        this.accountWriter = accountWriter;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.clock = clock;
    }

    // TODO Add refresh token support.
    @Transactional()
    @AuditedCommand(AuthenticationAuditPolicy.class)
    public Result<LoginResult> login(LoginCommand command) {
        Instant now = clock.now();

        String username = command.username();
        String password = command.password();

        LoginAccount account = accountReader.findByUsername(username).orElse(null);

        // 帳號不存在
        if (account == null) {
            return Result.failure(new InvalidCredentialsDomainError());
        }

        // 嘗試太多次
        if (account.lockedUntil() != null && account.lockedUntil().isAfter(now)) {
            return Result.failure(new TooManyAttemptsDomainError());
        }

        // 輸入密碼錯誤
        if (!passwordEncoder.matches(password, account.passwordHash())) {
            // 失敗次數寫入資料庫
            FailedAttemptResult attempt =
                    accountWriter.recordFailedAttempt(
                            account.passwordId(), MAX_FAILED_ATTEMPTS, now.plus(LOCK_DURATION));

            if (attempt.locked()) {
                return Result.failure(new TooManyAttemptsDomainError());
            }

            return Result.failure(new InvalidCredentialsDomainError());
        }

        // 清洗更新次數
        accountWriter.resetFailedAttempts(account.passwordId());

        String accessToken =
                accessTokenService.createToken(
                        account.privateUserId(),
                        account.publicUserId(),
                        account.username());

        String refreshToken =
                refreshTokenService.createToken(
                        account.privateUserId(),
                        account.publicUserId(),
                        account.username());

        return Result.success(
                new LoginResult(
                        accessToken,
                        accessTokenService.expiresInSeconds(),
                        refreshToken,
                        refreshTokenService.expiresInSeconds()));
    }
}
