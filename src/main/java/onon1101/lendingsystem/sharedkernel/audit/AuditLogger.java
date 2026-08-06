package onon1101.lendingsystem.sharedkernel.audit;

import onon1101.lendingsystem.security.AccountReferenceEncoder;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public AuditLogger(
            AccountReferenceEncoder accountReferenceEncoder,
            AuditSink auditSink
    ) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void handleSuccess(
            String eventType,
            AuditEventAttribute attribute
    ) {
        append(eventType, AuditOutcome.SUCCESS, attribute, null);
    }

    public void handleRejected(
            String eventType,
            AuditEventAttribute attribute,
            String reason
    ) {
        append(eventType, AuditOutcome.REJECTED, attribute, reason);
    }

    public void handleFailed(
            String eventType,
            AuditEventAttribute attribute,
            String reason
    ) {
        append(eventType, AuditOutcome.REJECTED, attribute, reason);
    }

    private void append(
            String eventType,
            AuditOutcome outcome,
            AuditEventAttribute attribute,
            String reason
    ) {
        Map<String, String> attributes =
                reason == null
                        ? Map.of(attribute.Key(), encode(attribute.Value()))
                        : Map.of(attribute.Key(), encode(attribute.Value()), "reason", reason);
        auditSink.append(new AuditRecord(eventType, outcome, attributes));
    }

    private String encode(
            String account
    ) {
        try {
            return accountReferenceEncoder.encode(account);
        } catch (RuntimeException ignored) {
            return "unavailable";
        }
    }
}
