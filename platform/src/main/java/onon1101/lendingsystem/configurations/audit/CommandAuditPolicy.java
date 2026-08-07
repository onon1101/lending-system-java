package onon1101.lendingsystem.configurations.audit;

import onon1101.lendingsystem.configurations.services.Command;
import onon1101.lendingsystem.configurations.services.CommandResult;

/** Converts an intercepted command outcome into an audit event. */
public interface CommandAuditPolicy<C extends Command, T extends CommandResult, E extends AuditEvent> {

    E onReturned(C command, T result);

    E onThrown(C command, Throwable throwable);
}
