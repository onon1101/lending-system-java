package onon1101.lendingsystem.user.register.audit;

import java.util.Objects;
import java.util.UUID;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;

/** Audit facts produced after a registration command has completed. */
public sealed interface RegistrationAuditEvent extends AuditEvent {

    String normalizedUsername();

    record Succeeded(String normalizedUsername, UUID publicUserId)
            implements RegistrationAuditEvent {
        public Succeeded {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
            Objects.requireNonNull(publicUserId, "Public user ID must not be null");
        }

        @Override
        public String eventType() {
            return "registration_succeeded";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.SUCCESS;
        }
    }

    record Failed(String normalizedUsername, String reason) implements RegistrationAuditEvent {
        public Failed {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
            Objects.requireNonNull(reason, "Failure reason must not be null");
        }

        @Override
        public String eventType() {
            return "registration_failed";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.REJECTED;
        }
    }
}
