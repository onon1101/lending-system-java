package onon1101.lendingsystem.configurations.audit;

import java.util.Map;
import java.util.Objects;

/** Infrastructure-neutral representation written to an audit destination. */
public record AuditRecord(String eventType, AuditOutcome outcome, Map<String, String> attributes) {

    public AuditRecord {
        Objects.requireNonNull(eventType, "Event type must not be null");
        Objects.requireNonNull(outcome, "Audit outcome must not be null");
        attributes = Map.copyOf(attributes);
    }
}
