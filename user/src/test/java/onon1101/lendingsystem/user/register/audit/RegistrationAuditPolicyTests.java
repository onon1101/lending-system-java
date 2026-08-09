package onon1101.lendingsystem.user.register.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.user.register.RegisterCommand;
import onon1101.lendingsystem.user.register.RegisterResult;
import onon1101.lendingsystem.user.register.error.InvalidEmailDomainError;
import org.junit.jupiter.api.Test;

class RegistrationAuditPolicyTests {

    private final RegistrationAuditPolicy policy = new RegistrationAuditPolicy();

    @Test
    void mapsSuccessfulResult() {
        UUID userId = UUID.randomUUID();

        Object event =
                policy.onReturned(command(" Alice "), Result.success(new RegisterResult(userId)));

        assertSuccess(event, "registration_succeeded", "publicUserIdRef", userId.toString());
    }

    @Test
    void mapsKnownBusinessFailure() {
        Object event =
                policy.onReturned(command("Alice"), Result.failure(new InvalidEmailDomainError()));

        assertRejected(event, "registration_failed", "accountRef", "alice", "User.InvalidEmail");
    }

    @Test
    void mapsUnexpectedExceptionWithoutExposingItsMessage() {
        Object event = policy.onThrown(command("Alice"), new RuntimeException("sensitive details"));

        assertFailed(event, "registration_failed", "accountRef", "alice", "system_error");
    }

    private RegisterCommand command(String username) {
        return new RegisterCommand(username, "password", "alice@example.com");
    }

    private void assertSuccess(
            Object event, String eventType, String attributeKey, String attributeValue) {
        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Success.class,
                        success -> {
                            assertThat(success.eventType()).isEqualTo(eventType);
                            assertThat(success.attribute().get(0).Key()).isEqualTo(attributeKey);
                            assertThat(success.attribute().get(0).Value()).isEqualTo(attributeValue);
                        });
    }

    private void assertRejected(
            Object event,
            String eventType,
            String attributeKey,
            String attributeValue,
            String reason) {
        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Rejected.class,
                        rejected -> {
                            assertThat(rejected.eventType()).isEqualTo(eventType);
                            assertThat(rejected.attribute().get(0).Key()).isEqualTo(attributeKey);
                            assertThat(rejected.attribute().get(0).Value()).isEqualTo(attributeValue);
                            assertThat(rejected.reason()).isEqualTo(reason);
                        });
    }

    private void assertFailed(
            Object event,
            String eventType,
            String attributeKey,
            String attributeValue,
            String reason) {
        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Failed.class,
                        failed -> {
                            assertThat(failed.eventType()).isEqualTo(eventType);
                            assertThat(failed.attribute().get(0).Key()).isEqualTo(attributeKey);
                            assertThat(failed.attribute().get(0).Value()).isEqualTo(attributeValue);
                            assertThat(failed.reason()).isEqualTo(reason);
                        });
    }
}
