package onon1101.lendingsystem.configurations.audit;

import java.util.List;
import java.util.Objects;
import onon1101.lendingsystem.configurations.audit.eventAttributes.AuditEventAttribute;

public interface AuditEvent {
    record Success(String eventType, List<AuditEventAttribute> attribute) implements AuditEvent {
        public Success {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            attribute = List.copyOf(attribute);
        }
    }

    record Rejected(String eventType, List<AuditEventAttribute> attribute, String reason)
            implements AuditEvent {
        public Rejected {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            attribute = List.copyOf(attribute);
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }

    record Failed(String eventType, List<AuditEventAttribute> attribute, String reason)
            implements AuditEvent {
        public Failed {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            attribute = List.copyOf(attribute);
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }
}
