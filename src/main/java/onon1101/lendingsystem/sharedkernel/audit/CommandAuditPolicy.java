package onon1101.lendingsystem.sharedkernel.audit;

/** Converts command arguments and outcomes into a feature-owned audit event. */
public interface CommandAuditPolicy {

    Object onReturned(Object[] arguments, Object result);

    Object onThrown(Object[] arguments, Throwable throwable);
}
