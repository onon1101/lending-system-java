package onon1101.lendingsystem.sharedkernel.audit;

import onon1101.lendingsystem.sharedkernel.Command;
import onon1101.lendingsystem.sharedkernel.CommandResult;

/** Converts an intercepted command outcome into an audit event. */
public interface CommandAuditPolicy<C extends Command, T extends CommandResult, E extends AuditEvent> {

    E onReturned(C command, T result);

    E onThrown(C command, Throwable throwable);
}
