package onon1101.lendingsystem.configurations.audit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import onon1101.lendingsystem.configurations.audit.eventAttributes.AuditEventAttribute;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public AuditLogger(AccountReferenceEncoder accountReferenceEncoder, AuditSink auditSink) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void handleSuccess(String eventType, List<AuditEventAttribute> attributes) {
        append(eventType, AuditOutcome.SUCCESS, attributes, null);
    }

    public void handleRejected(
            String eventType, List<AuditEventAttribute> attributes, String reason) {
        append(eventType, AuditOutcome.REJECTED, attributes, reason);
    }

    public void handleFailed(
            String eventType, List<AuditEventAttribute> attributes, String reason) {
        append(eventType, AuditOutcome.ERROR, attributes, reason);
    }

    private void append(
            String eventType,
            AuditOutcome outcome,
            List<AuditEventAttribute> eventAttributes,
            String reason) {
        Map<String, String> attributes = new LinkedHashMap<>();
        for (AuditEventAttribute attribute : eventAttributes) {
            attributes.put(attribute.Key(), encode(attribute.Value()));
        }
        if (reason != null) {
            attributes.put("reason", reason);
        }
        auditSink.append(new AuditRecord(eventType, outcome, attributes));
    }

    private String encode(String account) {
        try {
            return accountReferenceEncoder.encode(account);
        } catch (RuntimeException ignored) {
            return "unavailable";
        }
    }
}
