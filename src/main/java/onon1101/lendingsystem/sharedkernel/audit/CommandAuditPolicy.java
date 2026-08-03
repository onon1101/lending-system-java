package onon1101.lendingsystem.sharedkernel.audit;

/** Converts an intercepted command outcome into a feature-owned audit event. */
public interface CommandAuditPolicy<E extends AuditEvent> {

    E onReturned(Object[] arguments, Object result);

    E onThrown(Object[] arguments, Throwable throwable);
}
