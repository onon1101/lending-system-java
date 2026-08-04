package onon1101.lendingsystem.auth.resetPassword.audit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordAuditEventListener {

    private final ResetPasswordAuditLogger auditLogger;

    public ResetPasswordAuditEventListener(
            ResetPasswordAuditLogger auditLogger
    ) {
        this.auditLogger = auditLogger;
    }

    @EventListener
    public void onSucceeded(
            ResetPasswordAuditEvent.Succeeded event
    ) {
        auditLogger.resetPasswordSuccess(event.normalizedEmail());
    }

    @EventListener
    public void onFailed(
            ResetPasswordAuditEvent.Failed event
    ) {
        auditLogger.resetPasswordFailed(event.normalizedEmail(), event.reason());
    }
}
