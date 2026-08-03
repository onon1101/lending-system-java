package onon1101.lendingsystem.sharedkernel.audit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

class CommandAuditAspectTests {

    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final TestPolicy policy = mock(TestPolicy.class);
    private final JoinPoint joinPoint = mock(JoinPoint.class);
    private final AuditedCommand annotation = mock(AuditedCommand.class);
    private final CommandAuditAspect aspect =
            new CommandAuditAspect(applicationContext, eventPublisher);

    @Test
    void publishesReturnedEventSelectedByAnnotationPolicy() {
        Object[] arguments = {"command"};
        Object result = new Object();
        TestEvent event = new TestEvent();
        when(annotation.value()).thenAnswer(ignored -> TestPolicy.class);
        when(applicationContext.getBean(TestPolicy.class)).thenReturn(policy);
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(policy.onReturned(arguments, result)).thenReturn(event);

        aspect.afterReturning(joinPoint, annotation, result);

        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void publishesThrownEventSelectedByAnnotationPolicy() {
        Object[] arguments = {"command"};
        RuntimeException failure = new RuntimeException("failure");
        TestEvent event = new TestEvent();
        when(annotation.value()).thenAnswer(ignored -> TestPolicy.class);
        when(applicationContext.getBean(TestPolicy.class)).thenReturn(policy);
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(policy.onThrown(arguments, failure)).thenReturn(event);

        aspect.afterThrowing(joinPoint, annotation, failure);

        verify(eventPublisher).publishEvent(event);
    }

    private static final class TestPolicy implements CommandAuditPolicy<TestEvent> {

        @Override
        public TestEvent onReturned(Object[] arguments, Object result) {
            return null;
        }

        @Override
        public TestEvent onThrown(Object[] arguments, Throwable throwable) {
            return null;
        }
    }

    private record TestEvent() implements AuditEvent {
        @Override
        public String eventType() {
            return "test";
        }

        @Override
        public AuditOutcome outcome() {
            return AuditOutcome.SUCCESS;
        }
    }
}
