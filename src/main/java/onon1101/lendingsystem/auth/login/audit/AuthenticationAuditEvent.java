package onon1101.lendingsystem.auth.login.audit;

import java.util.Objects;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;

/** Audit facts produced after an authentication command has completed. */
public sealed interface AuthenticationAuditEvent extends AuditEvent {

    record Succeeded(String normalizedUsername) implements AuthenticationAuditEvent {
        public Succeeded {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
        }

    }

    record Failed(String normalizedUsername, String reason) implements AuthenticationAuditEvent {
        public Failed {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
            Objects.requireNonNull(reason, "Failure reason must not be null");
        }

    }
}
