package onon1101.lendingsystem.sharedkernel.audit;

import java.util.Objects;

public interface AuditEvent {
    record Success(String eventType, AuditEventAttribute attribute) implements AuditEvent {
        public Success {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(attribute, "Attribute must not be null.");
        }
    }

    record Rejected(String eventType, AuditEventAttribute attribute, String reason)
            implements AuditEvent {
        public Rejected {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(attribute, "Attribute must not be null.");
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }

    record Failed(String eventType, AuditEventAttribute attribute, String reason)
            implements AuditEvent {
        public Failed {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(attribute, "Attribute must not be null.");
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }
}
