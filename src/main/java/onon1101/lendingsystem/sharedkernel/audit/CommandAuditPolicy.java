package onon1101.lendingsystem.sharedkernel.audit;

import onon1101.lendingsystem.sharedkernel.ICommand;
import onon1101.lendingsystem.sharedkernel.IResult;

/** Converts an intercepted command outcome into a feature-owned audit event. */
public interface CommandAuditPolicy<C extends ICommand, T extends IResult, E extends AuditEvent> {

    E onReturned(C command, T result);

    E onThrown(C command, Throwable throwable);
}
