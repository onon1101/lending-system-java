package onon1101.lendingsystem.sharedkernel.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares which policy converts a command outcome into an audit event. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditedCommand {

    Class<? extends CommandAuditPolicy> value();
}
