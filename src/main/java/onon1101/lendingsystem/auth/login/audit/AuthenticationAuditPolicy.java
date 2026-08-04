package onon1101.lendingsystem.auth.login.audit;

import onon1101.lendingsystem.auth.login.LoginCommand;
import onon1101.lendingsystem.auth.login.LoginResult;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.springframework.stereotype.Component;

/** Maps authentication command outcomes to authentication audit events. */
@Component
public final class AuthenticationAuditPolicy
        implements CommandAuditPolicy<LoginCommand, Result<LoginResult>, AuthenticationAuditEvent> {

    @Override
    public AuthenticationAuditEvent onReturned(LoginCommand command, Result<LoginResult> result) {
        String normalizedUsername = command.username();

        return switch (result) {
            case Result.Success<LoginResult> success ->
                    new AuthenticationAuditEvent.Succeeded(normalizedUsername);
            case Result.Failure<LoginResult> failure ->
                    new AuthenticationAuditEvent.Failed(normalizedUsername, failure.error().code());
        };
    }

    @Override
    public AuthenticationAuditEvent onThrown(LoginCommand command, Throwable throwable) {
        return new AuthenticationAuditEvent.Failed(command.username(), "system_error");
    }
}
