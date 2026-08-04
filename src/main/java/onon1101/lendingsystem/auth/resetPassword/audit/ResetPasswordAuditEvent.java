package onon1101.lendingsystem.auth.resetPassword.audit;

import java.util.Objects;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;

public sealed interface ResetPasswordAuditEvent
        extends AuditEvent {

    record Succeeded(String normalizedEmail)
            implements ResetPasswordAuditEvent {

        public Succeeded{
            Objects.requireNonNull(normalizedEmail, "Normalized email must not be null.");
        }

    }

    record Failed(String normalizedEmail, String reason) implements ResetPasswordAuditEvent{
        public Failed {
            Objects.requireNonNull(normalizedEmail, "Normalized username must not be null.");
            Objects.requireNonNull(reason, "reason must not be null.");
        }

    }


}
