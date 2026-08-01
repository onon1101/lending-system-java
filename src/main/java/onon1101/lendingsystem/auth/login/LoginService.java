package onon1101.lendingsystem.auth.login;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import onon1101.lendingsystem.auth.token.JwtTokenService;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;

@Service
public class LoginService {

    private static final DomainError INVALID_CREDENTIALS = new DomainError(
            "Auth.InvalidCredentials",
            "Username or password is incorrect.");

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
    public Result<LoginResult> login(
            String username,
            String password) {

        String normalizedUsername = username
                .trim()
                .toLowerCase(Locale.ROOT);

        LoginAccount account = accountReader
                .findByUsername(normalizedUsername)
                .orElse(null);

        if (account == null || !passwordEncoder.matches(
                password,
                account.passwordHash())) {
            return Result.failure(INVALID_CREDENTIALS);
        }

        String accessToken = tokenService.createToken(
                account.userId(),
                account.username());

        return Result.success(new LoginResult(
                accessToken,
                tokenService.expiresInSeconds()));
    }
}
