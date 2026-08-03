package onon1101.lendingsystem.auth.login.audit;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

/** Maps authentication command outcomes to authentication audit events. */
@Component
public final class AuthenticationAuditPolicy
        implements CommandAuditPolicy<AuthenticationAuditEvent> {

    @Override
    public AuthenticationAuditEvent onReturned(Object[] arguments, Object result) {
        String normalizedUsername = normalizeUsername(arguments);
        Result<?> commandResult = (Result<?>) result;

        return switch (commandResult) {
            case Result.Success<?> success ->
                    new AuthenticationAuditEvent.Succeeded(normalizedUsername);
            case Result.Failure<?> failure ->
                    new AuthenticationAuditEvent.Failed(
                            normalizedUsername, reasonFor(failure.error().code()));
        };
    }

    @Override
    public AuthenticationAuditEvent onThrown(Object[] arguments, Throwable throwable) {
        return new AuthenticationAuditEvent.Failed(normalizeUsername(arguments), "system_error");
    }

    private String normalizeUsername(Object[] arguments) {
        String username = (String) arguments[0];
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String reasonFor(String errorCode) {
        return switch (errorCode) {
            case "Auth.InvalidCredentials" -> "invalid_credentials";
            case "Auth.TooManyAttempts" -> "too_many_attempts";
            default -> "business_error";
        };
    }
}
