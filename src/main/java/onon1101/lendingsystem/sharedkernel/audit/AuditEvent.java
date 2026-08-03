package onon1101.lendingsystem.sharedkernel.audit;

public interface AuditEvent {
    String eventType();

    AuditOutcome outcome();
}
