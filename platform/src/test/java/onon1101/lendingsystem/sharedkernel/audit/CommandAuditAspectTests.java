package onon1101.lendingsystem.sharedkernel.audit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import onon1101.lendingsystem.sharedkernel.Command;
import onon1101.lendingsystem.sharedkernel.CommandResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

class CommandAuditAspectTests {

    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final TestPolicy policy = mock(TestPolicy.class);
    private final JoinPoint joinPoint = mock(JoinPoint.class);
    private final MethodSignature signature = mock(MethodSignature.class);
    private final AuditedCommand annotation = mock(AuditedCommand.class);
    private final CommandAuditAspect aspect =
            new CommandAuditAspect(applicationContext, eventPublisher);

    @Test
    void publishesReturnedEventSelectedByAnnotationPolicy() throws NoSuchMethodException {
        TestCommand command = new TestCommand();
        TestResult result = new TestResult();
        TestEvent event = new TestEvent();
        Method method = TestService.class.getDeclaredMethod("handle", TestCommand.class);
        when(annotation.value()).thenAnswer(ignored -> TestPolicy.class);
        when(applicationContext.getBean(TestPolicy.class)).thenReturn(policy);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[] {command});
        when(policy.onReturned(command, result)).thenReturn(event);

        aspect.afterReturning(joinPoint, annotation, result);

        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void publishesThrownEventSelectedByAnnotationPolicy() throws NoSuchMethodException {
        TestCommand command = new TestCommand();
        RuntimeException failure = new RuntimeException("failure");
        TestEvent event = new TestEvent();
        Method method = TestService.class.getDeclaredMethod("handle", TestCommand.class);
        when(annotation.value()).thenAnswer(ignored -> TestPolicy.class);
        when(applicationContext.getBean(TestPolicy.class)).thenReturn(policy);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[] {command});
        when(policy.onThrown(command, failure)).thenReturn(event);

        aspect.afterThrowing(joinPoint, annotation, failure);

        verify(eventPublisher).publishEvent(event);
    }

    private static final class TestPolicy
            implements CommandAuditPolicy<TestCommand, TestResult, TestEvent> {

        @Override
        public TestEvent onReturned(TestCommand command, TestResult result) {
            return null;
        }

        @Override
        public TestEvent onThrown(TestCommand command, Throwable throwable) {
            return null;
        }
    }

    private static final class TestService {

        TestResult handle(TestCommand command) {
            return new TestResult();
        }
    }

    private record TestCommand() implements Command {}

    private record TestResult() implements CommandResult {}

    private record TestEvent() implements AuditEvent {}
}
