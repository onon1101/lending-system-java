package onon1101.lendingsystem.auth.login.audit;

import onon1101.lendingsystem.auth.login.AuthenticationAuditLogger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Translates authentication audit events into security audit log entries. */
@Component
public class AuthenticationAuditEventListener {

    private final AuthenticationAuditLogger auditLogger;

    public AuthenticationAuditEventListener(AuthenticationAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @EventListener
    public void onSucceeded(AuthenticationAuditEvent.Succeeded event) {
        auditLogger.loginSuccess(event.normalizedUsername());
    }

    @EventListener
    public void onFailed(AuthenticationAuditEvent.Failed event) {
        auditLogger.loginFailed(event.normalizedUsername());
    }
}
