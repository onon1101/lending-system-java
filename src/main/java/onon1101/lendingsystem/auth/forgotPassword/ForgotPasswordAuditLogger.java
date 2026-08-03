package onon1101.lendingsystem.auth.forgotPassword;

import java.util.Map;
import onon1101.lendingsystem.security.AccountReferenceEncoder;
import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;
import onon1101.lendingsystem.sharedkernel.audit.AuditRecord;
import onon1101.lendingsystem.sharedkernel.audit.AuditSink;
import org.springframework.stereotype.Component;

/** Maps password-reset audit facts to infrastructure-neutral records. */
@Component
public class ForgotPasswordAuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public ForgotPasswordAuditLogger(
            AccountReferenceEncoder accountReferenceEncoder, AuditSink auditSink) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void passwordResetRequested(String email) {
        append("password_reset_requested", AuditOutcome.SUCCESS, email, null);
    }

    public void passwordResetRejected(String email, String reason) {
        append("password_reset_rejected", AuditOutcome.REJECTED, email, reason);
    }

    public void passwordResetFailed(String email, String reason) {
        append("password_reset_failed", AuditOutcome.ERROR, email, reason);
    }

    private void append(String eventType, AuditOutcome outcome, String account, String reason) {
        Map<String, String> attributes =
                reason == null
                        ? Map.of("accountRef", encode(account))
                        : Map.of("accountRef", encode(account), "reason", reason);
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
