package onon1101.lendingsystem.auth.forgotPassword.audit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordAuditEventListener {

    private final ForgotPasswordAuditLogger auditLogger;

    public ForgotPasswordAuditEventListener(ForgotPasswordAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @EventListener
    public void onRequested(ForgotPasswordAuditEvent.Requested event) {
        auditLogger.passwordResetRequested(event.normalizedEmail());
    }

    @EventListener
    public void onRejected(ForgotPasswordAuditEvent.Rejected event) {
        auditLogger.passwordResetRejected(event.normalizedEmail(), event.reason());
    }

    @EventListener
    public void onFailed(ForgotPasswordAuditEvent.Failed event) {
        auditLogger.passwordResetFailed(event.normalizedEmail(), event.reason());
    }
}
