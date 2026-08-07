package onon1101.lendingsystem.sharedkernel.audit;

/** Output port for audit records. */
public interface AuditSink {

    void append(AuditRecord record);
}
