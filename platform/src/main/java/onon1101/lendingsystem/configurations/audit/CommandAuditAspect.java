package onon1101.lendingsystem.configurations.audit;

import java.lang.reflect.Method;
import onon1101.lendingsystem.configurations.services.Command;
import onon1101.lendingsystem.configurations.services.CommandResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
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

        ValidatedInvocation invocation =
                validateInvocation(joinPoint, auditedCommand, result, true);

        CommandAuditPolicy<?, ?, ?> policy = applicationContext.getBean(auditedCommand.value());

        AuditEvent event = invokeReturned(policy, invocation.command(), invocation.result());

        publish(event);
    }

    @AfterThrowing(
            pointcut = "@annotation(auditedCommand)",
            throwing = "throwable",
            argNames = "joinPoint,auditedCommand,throwable")
    public void afterThrowing(
            JoinPoint joinPoint, AuditedCommand auditedCommand, Throwable throwable) {

        ValidatedInvocation invocation = validateInvocation(joinPoint, auditedCommand, null, false);

        CommandAuditPolicy<?, ?, ?> policy = applicationContext.getBean(auditedCommand.value());

        AuditEvent event = invokeThrown(policy, invocation.command(), throwable);

        publish(event);
    }

    private ValidatedInvocation validateInvocation(
            JoinPoint joinPoint,
            AuditedCommand auditedCommand,
            Object result,
            boolean validateResult) {

        Method method = resolveMethod(joinPoint);

        if (method.getParameterCount() != 1) {
            throw new IllegalStateException(
                    "@AuditedCommand method must have exactly one command parameter: "
                            + method.toGenericString());
        }

        ResolvableType methodCommandType = ResolvableType.forMethodParameter(method, 0);

        ResolvableType methodResultType = ResolvableType.forMethodReturnType(method);

        ResolvableType policyType =
                ResolvableType.forClass(auditedCommand.value()).as(CommandAuditPolicy.class);

        ResolvableType policyCommandType = policyType.getGeneric(0);
        ResolvableType policyResultType = policyType.getGeneric(1);

        validateResolvedType("command", method, methodCommandType, policyCommandType);

        validateResolvedType("result", method, methodResultType, policyResultType);

        Object[] arguments = joinPoint.getArgs();

        if (arguments.length != 1) {
            throw new IllegalStateException(
                    "Expected exactly one intercepted argument for "
                            + method.toGenericString()
                            + ", but received "
                            + arguments.length);
        }

        Class<?> commandClass = methodCommandType.resolve();

        if (commandClass == null) {
            throw new IllegalStateException(
                    "Cannot resolve command type for " + method.toGenericString());
        }

        Object commandValue = commandClass.cast(arguments[0]);

        if (!(commandValue instanceof Command command)) {
            throw new IllegalStateException(
                    "Command type must implement ICommand: " + commandClass.getTypeName());
        }

        if (!validateResult) {
            return new ValidatedInvocation(command, null);
        }

        Class<?> resultClass = methodResultType.resolve();

        if (resultClass == null) {
            throw new IllegalStateException(
                    "Cannot resolve result type for " + method.toGenericString());
        }

        if (result == null) {
            throw new IllegalStateException(
                    "@AuditedCommand method returned null: " + method.toGenericString());
        }

        Object resultValue = resultClass.cast(result);

        if (!(resultValue instanceof CommandResult typedResult)) {
            throw new IllegalStateException(
                    "Result type must implement IResult: " + resultClass.getTypeName());
        }

        return new ValidatedInvocation(command, typedResult);
    }

    private void validateResolvedType(
            String typeName, Method method, ResolvableType methodType, ResolvableType policyType) {

        if (methodType == ResolvableType.NONE || methodType.resolve() == null) {
            throw new IllegalStateException(
                    "Cannot resolve " + typeName + " type for " + method.toGenericString());
        }

        if (policyType == ResolvableType.NONE || policyType.resolve() == null) {
            throw new IllegalStateException(
                    "Cannot resolve audit policy "
                            + typeName
                            + " type for "
                            + method.toGenericString());
        }

        boolean sameType =
                methodType.isAssignableFrom(policyType) && policyType.isAssignableFrom(methodType);

        if (!sameType) {
            throw new IllegalStateException(
                    "Audit policy "
                            + typeName
                            + " type mismatch for "
                            + method.toGenericString()
                            + ": method declares "
                            + methodType
                            + ", but policy declares "
                            + policyType);
        }
    }

    private Method resolveMethod(JoinPoint joinPoint) {
        Method signatureMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Object target = joinPoint.getTarget();

        if (target == null) {
            return BridgeMethodResolver.findBridgedMethod(signatureMethod);
        }

        Method specificMethod = AopUtils.getMostSpecificMethod(signatureMethod, target.getClass());

        return BridgeMethodResolver.findBridgedMethod(specificMethod);
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

    private record ValidatedInvocation(Command command, CommandResult result) {}
}
