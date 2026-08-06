package onon1101.lendingsystem.auth.resetPassword.audit;

import static org.assertj.core.api.Assertions.assertThat;

import onon1101.lendingsystem.auth.resetPassword.ResetPasswordCommand;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResult;
import onon1101.lendingsystem.auth.resetPassword.error.InvalidResetTokenDomainError;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.junit.jupiter.api.Test;

class ResetPasswordAuditPolicyTests {

    private final ResetPasswordAuditPolicy policy = new ResetPasswordAuditPolicy();

    @Test
    void mapsSuccessfulResult() {
        Object event =
                policy.onReturned(
                        command(), Result.success(new ResetPasswordResult("user@example.com")));

        assertSuccess(
                event, "password_reset_receiver_successed", "emailRef", "user@example.com");
    }

    @Test
    void mapsKnownBusinessFailure() {
        Object event =
                policy.onReturned(command(), Result.failure(new InvalidResetTokenDomainError()));

        assertRejected(
                event,
                "password_reset_receiver_failed",
                "emailRef",
                "unavailable",
                "ResetPassword.InvalidResetToken");
    }

    @Test
    void mapsUnexpectedException() {
        Object event = policy.onThrown(command(), new RuntimeException("sensitive details"));

        assertFailed(
                event, "password_reset_receiver_failed", "emailRef", "unavailable", "system_error");
    }

    private ResetPasswordCommand command() {
        return new ResetPasswordCommand("reset-token", "new-secure-password");
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
                            assertThat(rejected.attribute().Key()).isEqualTo(attributeKey);
                            assertThat(rejected.attribute().Value()).isEqualTo(attributeValue);
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
                            assertThat(failed.attribute().Key()).isEqualTo(attributeKey);
                            assertThat(failed.attribute().Value()).isEqualTo(attributeValue);
                            assertThat(failed.reason()).isEqualTo(reason);
                        });
    }
}
