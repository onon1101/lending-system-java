package onon1101.lendingsystem.configurations.audit;

import onon1101.lendingsystem.configurations.services.Command;
import onon1101.lendingsystem.configurations.services.CommandResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** Publishes audit events when a command publisher actually terminates. */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CommandAuditAspect {

    private final ApplicationContext applicationContext;
    private final ApplicationEventPublisher eventPublisher;

    public CommandAuditAspect(
            ApplicationContext applicationContext, ApplicationEventPublisher eventPublisher) {
        this.applicationContext = applicationContext;
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(auditedCommand)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditedCommand auditedCommand)
            throws Throwable {
        Command command = requireCommand(joinPoint);
        CommandAuditPolicy<?, ?, ?> policy = applicationContext.getBean(auditedCommand.value());

        Object returned;
        try {
            returned = joinPoint.proceed();
        } catch (Throwable throwable) {
            publish(invokeThrown(policy, command, throwable));
            throw throwable;
        }

        if (returned instanceof Mono<?> mono) {
            return mono.doOnNext(
                            result ->
                                    publish(invokeReturned(policy, command, requireResult(result))))
                    .doOnError(throwable -> publish(invokeThrown(policy, command, throwable)));
        }

        publish(invokeReturned(policy, command, requireResult(returned)));
        return returned;
    }

    private Command requireCommand(ProceedingJoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();
        if (arguments.length != 1 || !(arguments[0] instanceof Command command)) {
            throw new IllegalStateException(
                    "@AuditedCommand method must have exactly one Command argument: "
                            + joinPoint.getSignature().toLongString());
        }
        return command;
    }

    private CommandResult requireResult(Object result) {
        if (!(result instanceof CommandResult commandResult)) {
            throw new IllegalStateException("@AuditedCommand publisher must emit a CommandResult");
        }
        return commandResult;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private AuditEvent invokeReturned(
            CommandAuditPolicy<?, ?, ?> policy, Command command, CommandResult result) {
        return ((CommandAuditPolicy) policy).onReturned(command, result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private AuditEvent invokeThrown(
            CommandAuditPolicy<?, ?, ?> policy, Command command, Throwable throwable) {
        return ((CommandAuditPolicy) policy).onThrown(command, throwable);
    }

    private void publish(AuditEvent event) {
        if (event != null) {
            eventPublisher.publishEvent(event);
        }
    }
}
