package onon1101.lendingsystem.auth.login.audit;

import java.util.Objects;

/** Audit facts produced after an authentication command has completed. */
public sealed interface AuthenticationAuditEvent {

    String normalizedUsername();

    record Succeeded(String normalizedUsername) implements AuthenticationAuditEvent {

        public Succeeded {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
        }
    }

    record Failed(String normalizedUsername) implements AuthenticationAuditEvent {

        public Failed {
            Objects.requireNonNull(normalizedUsername, "Normalized username must not be null");
        }
    }
}
