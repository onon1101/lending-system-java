package onon1101.lendingsystem.user.register.audit;

import onon1101.lendingsystem.user.register.RegistrationAuditLogger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Translates registration audit events into security audit log entries. */
@Component
public class RegistrationAuditEventListener {

    private final RegistrationAuditLogger auditLogger;

    public RegistrationAuditEventListener(RegistrationAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @EventListener
    public void onSucceeded(RegistrationAuditEvent.Succeeded event) {
        auditLogger.registerSuccess(event.normalizedUsername(), event.publicUserId());
    }

    @EventListener
    public void onFailed(RegistrationAuditEvent.Failed event) {
        auditLogger.registerFailed(event.normalizedUsername(), event.reason());
    }
}
