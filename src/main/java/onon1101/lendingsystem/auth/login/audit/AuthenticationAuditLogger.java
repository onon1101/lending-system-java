package onon1101.lendingsystem.auth.login.audit;

import java.util.Map;
import onon1101.lendingsystem.security.AccountReferenceEncoder;
import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;
import onon1101.lendingsystem.sharedkernel.audit.AuditRecord;
import onon1101.lendingsystem.sharedkernel.audit.AuditSink;
import org.springframework.stereotype.Component;

/** Maps authentication audit facts to infrastructure-neutral records. */
@Component
public class AuthenticationAuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public AuthenticationAuditLogger(
            AccountReferenceEncoder accountReferenceEncoder, AuditSink auditSink) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void loginFailed(String username, String reason) {
        auditSink.append(
                new AuditRecord(
                        "authentication_failed",
                        AuditOutcome.REJECTED,
                        Map.of("accountRef", encode(username), "reason", reason)));
    }

    public void loginSuccess(String username) {
        auditSink.append(
                new AuditRecord(
                        "authentication_succeeded",
                        AuditOutcome.SUCCESS,
                        Map.of("accountRef", encode(username))));
    }

    private String encode(String account) {
        try {
            return accountReferenceEncoder.encode(account);
        } catch (RuntimeException ignored) {
            return "unavailable";
        }
    }
}
