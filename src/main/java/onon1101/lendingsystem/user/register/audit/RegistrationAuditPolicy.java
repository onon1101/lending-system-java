package onon1101.lendingsystem.user.register.audit;

import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.audit.UserPublicIdAuditEventAttribute;
import onon1101.lendingsystem.sharedkernel.audit.UsernameAuditEventAttribute;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.user.register.RegisterCommand;
import onon1101.lendingsystem.user.register.RegisterResult;
import org.springframework.stereotype.Component;

/** Maps registration command outcomes to registration audit events. */
@Component
public final class RegistrationAuditPolicy
        implements CommandAuditPolicy<RegisterCommand, Result<RegisterResult>, AuditEvent> {

    @Override
    public AuditEvent onReturned(
            RegisterCommand command, Result<RegisterResult> result) {
        String normalizedUsername = command.username();

        return switch (result) {
            case Result.Success<RegisterResult> success ->
                    new AuditEvent.Success(
                            "registration_succeeded",
                            new UserPublicIdAuditEventAttribute(success.value().userId()));
            case Result.Failure<RegisterResult> failure ->
                    new AuditEvent.Rejected(
                            "registration_failed",
                            new UsernameAuditEventAttribute(normalizedUsername),
                            failure.error().code());
        };
    }

    @Override
    public AuditEvent onThrown(RegisterCommand command, Throwable throwable) {
        return new AuditEvent.Failed(
                "registration_failed",
                new UsernameAuditEventAttribute(command.username()),
                "system_error");
    }
}
