package onon1101.lendingsystem.user.register.audit;

import onon1101.lendingsystem.sharedkernel.audit.CommandAuditPolicy;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.user.register.RegisterCommand;
import onon1101.lendingsystem.user.register.RegisterResult;
import org.springframework.stereotype.Component;

/** Maps registration command outcomes to registration audit events. */
@Component
public final class RegistrationAuditPolicy
        implements CommandAuditPolicy<
                RegisterCommand, Result<RegisterResult>, RegistrationAuditEvent> {

    @Override
    public RegistrationAuditEvent onReturned(
            RegisterCommand command, Result<RegisterResult> result) {
        String normalizedUsername = command.username();

        return switch (result) {
            case Result.Success<RegisterResult> success ->
                    new RegistrationAuditEvent.Succeeded(
                            normalizedUsername, success.value().userId());
            case Result.Failure<RegisterResult> failure ->
                    new RegistrationAuditEvent.Failed(normalizedUsername, failure.error().code());
        };
    }

    @Override
    public RegistrationAuditEvent onThrown(RegisterCommand command, Throwable throwable) {
        return new RegistrationAuditEvent.Failed(command.username(), "system_error");
    }
}
