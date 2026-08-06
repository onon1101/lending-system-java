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
            UUID publicUserid
    ) {
        append(eventType, AuditOutcome.SUCCESS, publicUserid, null);
    }

    public void handleRejected(
            String eventType,
            UUID publicUserId,
            String reason
    ) {
        append(eventType, AuditOutcome.REJECTED, publicUserId, reason);
    }

    public void handleFailed(
            String eventType,
            UUID publicUserId,
            String reason
    ) {
        append(eventType, AuditOutcome.REJECTED, publicUserId, reason);
    }

    private void append(
            String eventType,
            AuditOutcome outcome,
            UUID account,
            String reason
    ) {
        String accountStr = account.toString();
        Map<String, String> attributes =
                reason == null
                        ? Map.of("accountRef", encode(accountStr))
                        : Map.of("accountRef", encode(accountStr), "reason", reason);
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
