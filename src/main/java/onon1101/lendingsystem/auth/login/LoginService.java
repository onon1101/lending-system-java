package onon1101.lendingsystem.auth.login;

import java.util.Locale;
import onon1101.lendingsystem.auth.login.audit.AuthenticationAuditPolicy;
import onon1101.lendingsystem.auth.token.JwtTokenService;
import onon1101.lendingsystem.sharedkernel.audit.AuditedCommand;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private static final DomainError INVALID_CREDENTIALS =
            new DomainError("Auth.InvalidCredentials", "Username or password is incorrect.");

    private final LoginAccountReader accountReader;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;

    public LoginService(
            LoginAccountReader accountReader,
            PasswordEncoder passwordEncoder,
            JwtTokenService tokenService) {
        this.accountReader = accountReader;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // TODO Add refresh token support.
    @Transactional(readOnly = true)
    @AuditedCommand(AuthenticationAuditPolicy.class)
    public Result<LoginResult> login(String username, String password) {

        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);

        LoginAccount account = accountReader.findByUsername(normalizedUsername).orElse(null);

        if (account == null || !passwordEncoder.matches(password, account.passwordHash())) {

            //todo: 需要將失敗次數寫入資料庫
            return Result.failure(INVALID_CREDENTIALS);
        }

        String accessToken = tokenService.createToken(account.publicUserId(), account.username());

        return Result.success(new LoginResult(accessToken, tokenService.expiresInSeconds()));
    }
}
