package onon1101.lendingsystem.application.audit;

/** Output port for audit records. */
public interface AuditSink {

    void append(AuditRecord record);
}
