package onon1101.lendingsystem.user.register.audit;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.user.register.RegisterResult;
import org.springframework.stereotype.Component;

/** Maps registration command outcomes to registration audit events. */
@Component
public final class RegistrationAuditPolicy implements CommandAuditPolicy<RegistrationAuditEvent> {

    @Override
    public RegistrationAuditEvent onReturned(Object[] arguments, Object result) {
        String normalizedUsername = normalizeUsername(arguments);
        Result<?> commandResult = (Result<?>) result;

        return switch (commandResult) {
            case Result.Success<?> success -> {
                RegisterResult registration = (RegisterResult) success.value();
                yield new RegistrationAuditEvent.Succeeded(
                        normalizedUsername, registration.userId());
            }
            case Result.Failure<?> failure ->
                    new RegistrationAuditEvent.Failed(
                            normalizedUsername, reasonFor(failure.error().code()));
        };
    }

    @Override
    public RegistrationAuditEvent onThrown(Object[] arguments, Throwable throwable) {
        return new RegistrationAuditEvent.Failed(normalizeUsername(arguments), "system_error");
    }

    private String normalizeUsername(Object[] arguments) {
        String username = (String) arguments[0];
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String reasonFor(String errorCode) {
        return switch (errorCode) {
            case "User.InvalidEmail" -> "invalid_email";
            case "User.InvalidRegistration" -> "account_conflict";
            default -> "business_error";
        };
    }
}
