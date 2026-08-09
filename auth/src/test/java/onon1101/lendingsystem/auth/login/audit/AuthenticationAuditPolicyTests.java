package onon1101.lendingsystem.auth.login.audit;

import static org.assertj.core.api.Assertions.assertThat;

import onon1101.lendingsystem.auth.login.LoginCommand;
import onon1101.lendingsystem.auth.login.LoginResult;
import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.error.TooManyAttemptsDomainError;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.domain.Result;
import org.junit.jupiter.api.Test;

class AuthenticationAuditPolicyTests {

    private final AuthenticationAuditPolicy policy = new AuthenticationAuditPolicy();

    @Test
    void mapsSuccessfulResult() {
        Object event =
                policy.onReturned(
                        command(" Alice "), Result.success(new LoginResult("token", 300)));

        assertSuccess(event, "authentication_succeeded", "accountRef", "alice");
    }

    @Test
    void mapsFailedResult() {
        Object event =
                policy.onReturned(
                        command("Alice"), Result.failure(new InvalidCredentialsDomainError()));

        assertRejected(
                event, "authentication_failed", "accountRef", "alice", "Auth.InvalidCredentials");
    }

    @Test
    void mapsTooManyAttemptsFailure() {
        Object event =
                policy.onReturned(
                        command("Alice"), Result.failure(new TooManyAttemptsDomainError()));

        assertRejected(
                event, "authentication_failed", "accountRef", "alice", "Auth.TooManyAttempts");
    }

    @Test
    void mapsUnexpectedException() {
        Object event = policy.onThrown(command("Alice"), new RuntimeException("failure"));

        assertFailed(event, "authentication_failed", "accountRef", "alice", "system_error");
    }

    private LoginCommand command(String username) {
        return new LoginCommand(username, "password");
    }

    private void assertSuccess(
            Object event, String eventType, String attributeKey, String attributeValue) {
        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Success.class,
                        success -> {
                            assertThat(success.eventType()).isEqualTo(eventType);
                            assertThat(success.attribute().get(0).Key()).isEqualTo(attributeKey);
                            assertThat(success.attribute().get(0).Value())
                                    .isEqualTo(attributeValue);
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
                            assertThat(rejected.attribute().get(0).Value())
                                    .isEqualTo(attributeValue);
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
