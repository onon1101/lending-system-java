package onon1101.lendingsystem.sharedkernel.audit;

import java.util.Objects;
import java.util.UUID;

public interface AuditEvent {
    record Success(String eventType, UUID publicUserId) {
        public Success {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(publicUserId, "Public userid must not be null.");
        }
    }

    record Rejected(String eventType, UUID publicUserId, String reason) {
        public Rejected {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(publicUserId, "Public userid must not be null.");
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }

    record Failed(String eventType, UUID publicUserId, String reason) {
        public Failed {
            Objects.requireNonNull(eventType, "Event Type must not be null.");
            Objects.requireNonNull(publicUserId, "Public userid must not be null.");
            Objects.requireNonNull(reason, "Event of rejecting request must not be null.");
        }
    }
}
