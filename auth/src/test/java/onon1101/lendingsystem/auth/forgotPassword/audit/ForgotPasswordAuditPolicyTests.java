package onon1101.lendingsystem.auth.forgotPassword.audit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;
import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordCommand;
import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordResult;
import onon1101.lendingsystem.auth.forgotPassword.error.InvalidEmailDomainError;
import onon1101.lendingsystem.configurations.audit.AuditEvent;
import onon1101.lendingsystem.configurations.domain.Result;
import org.junit.jupiter.api.Test;

class ForgotPasswordAuditPolicyTests {

    private final ForgotPasswordAuditPolicy policy = new ForgotPasswordAuditPolicy();

    @Test
    void producesRequestedEventForSuccessfulRequest() {
        Object event =
                policy.onReturned(
                        new ForgotPasswordCommand(" User@Example.com "),
                        Result.success(new ForgotPasswordResult(UUID.randomUUID())));

        assertSuccess(event, "password_reset_sender_requested", "emailRef", "user@example.com");
    }

    @Test
    void producesRejectedEventForInvalidEmail() {
        Object event =
                policy.onReturned(
                        new ForgotPasswordCommand("invalid"),
                        Result.failure(new InvalidEmailDomainError()));

        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Rejected.class,
                        rejected -> {
                            assertThat(rejected.eventType())
                                    .isEqualTo("password_reset_sender_rejected");
                            assertThat(rejected.attribute().Key()).isEqualTo("emailRef");
                            assertThat(rejected.attribute().Value()).isEqualTo("invalid");
                            assertThat(rejected.reason())
                                    .isEqualTo("The provided email address is invalid");
                        });
    }

    @Test
    void producesFailedEventWhenCommandThrows() {
        Object event =
                policy.onThrown(
                        new ForgotPasswordCommand("User@Example.com"),
                        new RuntimeException("SMTP unavailable"));

        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Failed.class,
                        failed -> {
                            assertThat(failed.eventType())
                                    .isEqualTo("password_reset_sender_failed");
                            assertThat(failed.attribute().Key()).isEqualTo("emailRef");
                            assertThat(failed.attribute().Value()).isEqualTo("user@example.com");
                            assertThat(failed.reason()).isEqualTo("system_error");
                        });
    }

    private void assertSuccess(
            Object event, String eventType, String attributeKey, String attributeValue) {
        assertThat(event)
                .isInstanceOfSatisfying(
                        AuditEvent.Success.class,
                        success -> {
                            assertThat(success.eventType()).isEqualTo(eventType);
                            assertThat(success.attribute().Key()).isEqualTo(attributeKey);
                            assertThat(success.attribute().Value()).isEqualTo(attributeValue);
                        });
    }
}
