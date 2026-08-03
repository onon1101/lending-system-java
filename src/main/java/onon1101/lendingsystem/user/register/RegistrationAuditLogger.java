package onon1101.lendingsystem.user.register;

import java.util.Map;
import java.util.UUID;
import onon1101.lendingsystem.security.AccountReferenceEncoder;
import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;
import onon1101.lendingsystem.sharedkernel.audit.AuditRecord;
import onon1101.lendingsystem.sharedkernel.audit.AuditSink;
import org.springframework.stereotype.Component;

/** Maps registration audit facts to infrastructure-neutral records. */
@Component
public class RegistrationAuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public RegistrationAuditLogger(
            AccountReferenceEncoder accountReferenceEncoder, AuditSink auditSink) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void registerFailed(String username, String reason) {
        auditSink.append(
                new AuditRecord(
                        "registration_failed",
                        AuditOutcome.REJECTED,
                        Map.of("accountRef", encode(username), "reason", reason)));
    }

    public void registerSuccess(String username, UUID userId) {
        auditSink.append(
                new AuditRecord(
                        "registration_succeeded",
                        AuditOutcome.SUCCESS,
                        Map.of("accountRef", encode(username), "userId", userId.toString())));
    }

    private String encode(String account) {
        try {
            return accountReferenceEncoder.encode(account);
        } catch (RuntimeException ignored) {
            return "unavailable";
        }
    }
}
