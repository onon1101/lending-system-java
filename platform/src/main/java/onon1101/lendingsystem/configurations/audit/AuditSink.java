package onon1101.lendingsystem.configurations.audit;

/** Output port for audit records. */
public interface AuditSink {

    void append(AuditRecord record);
}
