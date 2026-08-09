package onon1101.lendingsystem.auth.login.audit;

import java.util.List;
import onon1101.lendingsystem.auth.login.LoginCommand;
import onon1101.lendingsystem.auth.login.LoginResult;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;
import onon1101.lendingsystem.configurations.audit.eventAttributes.UsernameAuditEventAttribute;
import onon1101.lendingsystem.configurations.domain.Result;
import org.springframework.stereotype.Component;

/** Maps authentication command outcomes to authentication audit events. */
@Component
public final class AuthenticationAuditPolicy
        implements CommandAuditPolicy<LoginCommand, Result<LoginResult>, AuditEvent> {

    @Override
    public AuditEvent onReturned(LoginCommand command, Result<LoginResult> result) {
        String normalizedUsername = command.username();

        return switch (result) {
            case Result.Success<LoginResult> success ->
                    new AuditEvent.Success(
                            "authentication_succeeded",
                            List.of(new UsernameAuditEventAttribute(normalizedUsername)));
            case Result.Failure<LoginResult> failure ->
                    new AuditEvent.Rejected(
                            "authentication_failed",
                            List.of(new UsernameAuditEventAttribute(normalizedUsername)),
                            failure.error().code());
        };
    }

    @Override
    public AuditEvent onThrown(LoginCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "authentication_failed",
                List.of(new UsernameAuditEventAttribute(command.username())),
                "system_error");
    }
}
