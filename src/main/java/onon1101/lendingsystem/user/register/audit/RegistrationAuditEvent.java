package onon1101.lendingsystem.user.register.audit;

import java.util.Objects;
import java.util.UUID;

/** Audit facts produced after a registration command has completed. */
public sealed interface RegistrationAuditEvent {

    String normalizedUsername();

    record Succeeded(String normalizedUsername, UUID publicUserId)
            implements RegistrationAuditEvent {

        public Succeeded {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
            Objects.requireNonNull(publicUserId, "Public user ID must not be null");
        }
    }

    record Failed(String normalizedUsername, String reason) implements RegistrationAuditEvent {

        public Failed {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
            Objects.requireNonNull(reason, "Failure reason must not be null");
        }
    }
}
