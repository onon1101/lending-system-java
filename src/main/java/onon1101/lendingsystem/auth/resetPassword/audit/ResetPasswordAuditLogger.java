package onon1101.lendingsystem.auth.resetPassword.audit;

import onon1101.lendingsystem.security.AccountReferenceEncoder;

import onon1101.lendingsystem.sharedkernel.audit.AuditOutcome;
import onon1101.lendingsystem.sharedkernel.audit.AuditRecord;
import onon1101.lendingsystem.sharedkernel.audit.AuditSink;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResetPasswordAuditLogger {

    private final AccountReferenceEncoder accountReferenceEncoder;
    private final AuditSink auditSink;

    public ResetPasswordAuditLogger(
            AccountReferenceEncoder accountReferenceEncoder,
            AuditSink auditSink
    ) {
        this.accountReferenceEncoder = accountReferenceEncoder;
        this.auditSink = auditSink;
    }

    public void resetPasswordSuccess(String email) {
        auditSink.append(
                new AuditRecord(
                        "password_reset_receiver_successed",
                        AuditOutcome.SUCCESS,
                        Map.of("accountRef", encode(email))
                ));
    }

    public void resetPasswordFailed(String email, String reason) {
        auditSink.append(
                new AuditRecord(
                        "password_reset_receiver_failed",
                        AuditOutcome.ERROR,
                        Map.of("accountRef", encode(email),
                                "reason", reason)
                )
        );
    }

    private String encode(String account) {
        try {
            return accountReferenceEncoder.encode(account);
        } catch (RuntimeException ignored) {
            return "unavailable";
        }
    }
}
