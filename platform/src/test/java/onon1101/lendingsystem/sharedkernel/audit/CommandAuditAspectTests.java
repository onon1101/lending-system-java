package onon1101.lendingsystem.sharedkernel.audit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.audit.CommandAuditAspect;
import onon1101.lendingsystem.configurations.audit.CommandAuditPolicy;
import onon1101.lendingsystem.configurations.services.Command;
import onon1101.lendingsystem.configurations.services.CommandResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

class CommandAuditAspectTests {

    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final TestPolicy policy = mock(TestPolicy.class);
    private final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    private final AuditedCommand annotation = mock(AuditedCommand.class);
    private final CommandAuditAspect aspect =
            new CommandAuditAspect(applicationContext, eventPublisher);

    @Test
    void publishesReturnedEventWhenMonoEmits() throws Throwable {
        TestCommand command = new TestCommand();
        TestResult result = new TestResult();
        TestEvent event = new TestEvent();
        arrange(command);
        when(joinPoint.proceed()).thenReturn(Mono.just(result));
        when(policy.onReturned(command, result)).thenReturn(event);

        @SuppressWarnings("unchecked")
        Mono<TestResult> audited = (Mono<TestResult>) aspect.audit(joinPoint, annotation);
        audited.block();

        verify(eventPublisher).publishEvent(event);
    }

    @Test
    void publishesThrownEventWhenMonoFails() throws Throwable {
        TestCommand command = new TestCommand();
        RuntimeException failure = new RuntimeException("failure");
        TestEvent event = new TestEvent();
        arrange(command);
        when(joinPoint.proceed()).thenReturn(Mono.error(failure));
        when(policy.onThrown(command, failure)).thenReturn(event);

        @SuppressWarnings("unchecked")
        Mono<TestResult> audited = (Mono<TestResult>) aspect.audit(joinPoint, annotation);
        assertThatThrownBy(audited::block).isSameAs(failure);

        verify(eventPublisher).publishEvent(event);
    }

    private void arrange(TestCommand command) {
        when(annotation.value()).thenAnswer(ignored -> TestPolicy.class);
        when(applicationContext.getBean(TestPolicy.class)).thenReturn(policy);
        when(joinPoint.getArgs()).thenReturn(new Object[] {command});
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

    private record TestCommand() implements Command {}

    private record TestResult() implements CommandResult {}

    private record TestEvent() implements AuditEvent {}
}
