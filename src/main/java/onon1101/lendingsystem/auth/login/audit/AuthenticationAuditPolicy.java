package onon1101.lendingsystem.auth.login.audit;

import java.util.Locale;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

/** Maps authentication command outcomes to authentication audit events. */
@Component
public class AuthenticationAuditPolicy implements CommandAuditPolicy {

    @Override
    public Object onReturned(Object[] arguments, Object result) {
        String normalizedUsername = normalizeUsername(arguments);
        Result<?> commandResult = (Result<?>) result;

        return commandResult.isSuccess()
                ? new AuthenticationAuditEvent.Succeeded(normalizedUsername)
                : new AuthenticationAuditEvent.Failed(normalizedUsername);
    }

    @Override
    public Object onThrown(Object[] arguments, Throwable throwable) {
        return new AuthenticationAuditEvent.Failed(normalizeUsername(arguments));
    }

    private String normalizeUsername(Object[] arguments) {
        String username = (String) arguments[0];
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
