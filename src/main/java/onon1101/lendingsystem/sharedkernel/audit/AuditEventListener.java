package onon1101.lendingsystem.sharedkernel.audit;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private final AuditLogger auditLogger;

    public AuditEventListener(
            AuditLogger auditLogger
    ) {
        this.auditLogger = auditLogger;
    }

    @EventListener
    public void onSuccess(AuditEvent.Success event) {
        auditLogger.handleSuccess(
                event.eventType(),
                event.publicUserId()
        );
    }

    @EventListener
    public void onRejected(AuditEvent.Rejected event) {
        auditLogger.handleRejected(
                event.eventType(),
                event.publicUserId(),
                event.reason()
        );
    }

    @EventListener
    public void onFailed(AuditEvent.Failed event) {
        auditLogger.handleFailed(
                event.eventType(),
                event.publicUserId(),
                event.reason()
        );
    }
}
