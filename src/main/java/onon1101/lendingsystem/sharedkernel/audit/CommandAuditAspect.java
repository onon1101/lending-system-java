package onon1101.lendingsystem.sharedkernel.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Publishes audit events after the command transaction has completed. */
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

    @AfterReturning(
            pointcut = "@annotation(auditedCommand)",
            returning = "result",
            argNames = "joinPoint,auditedCommand,result")
    public void afterReturning(JoinPoint joinPoint, AuditedCommand auditedCommand, Object result) {
        CommandAuditPolicy policy = applicationContext.getBean(auditedCommand.value());
        publish(policy.onReturned(joinPoint.getArgs(), result));
    }

    @AfterThrowing(
            pointcut = "@annotation(auditedCommand)",
            throwing = "throwable",
            argNames = "joinPoint,auditedCommand,throwable")
    public void afterThrowing(
            JoinPoint joinPoint, AuditedCommand auditedCommand, Throwable throwable) {
        CommandAuditPolicy policy = applicationContext.getBean(auditedCommand.value());
        publish(policy.onThrown(joinPoint.getArgs(), throwable));
    }

    private void publish(Object event) {
        if (event != null) {
            eventPublisher.publishEvent(event);
        }
    }
}
