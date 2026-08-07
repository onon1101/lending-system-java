package onon1101.lendingsystem.auth.resetPassword.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.auth.commons.PasswordProperties;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordCommand;
import onon1101.lendingsystem.auth.resetPassword.ResetPasswordResult;
import onon1101.lendingsystem.auth.resetPassword.error.InvalidResetTokenDomainError;
import onon1101.lendingsystem.sharedkernel.audit.AuditEvent;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.sharedkernel.token.JwtTokenService;
import onon1101.lendingsystem.sharedkernel.token.TokenPayload;
import org.junit.jupiter.api.Test;

class ResetPasswordAuditPolicyTests {

    private final JwtTokenService tokenService = mock(JwtTokenService.class);
    private final PasswordProperties passwordProperties =
            new PasswordProperties("issuer", Duration.ofMinutes(15));
    private final ResetPasswordAuditPolicy policy =
            new ResetPasswordAuditPolicy(tokenService, passwordProperties);

    @Test
    void mapsSuccessfulResult() {
        UUID publicUserId = UUID.randomUUID();
        givenTokenPublicUserId(publicUserId);

        Object event =
                policy.onReturned(
                        command(), Result.success(new ResetPasswordResult()));

        assertSuccess(
                event,
                "password_reset_receiver_successed",
                "publicUserIdRef",
                publicUserId.toString());
    }

    @Test
    void mapsKnownBusinessFailure() {
        UUID publicUserId = UUID.randomUUID();
        givenTokenPublicUserId(publicUserId);

        Object event =
                policy.onReturned(command(), Result.failure(new InvalidResetTokenDomainError()));

        assertRejected(
                event,
                "password_reset_receiver_failed",
                "publicUserIdRef",
                publicUserId.toString(),
                "ResetPassword.InvalidResetToken");
    }

    @Test
    void mapsUnexpectedExceptionWithUnparseableToken() {
        when(tokenService.decode("reset-token", passwordProperties))
                .thenThrow(new IllegalArgumentException("invalid token"));

        Object event = policy.onThrown(command(), new RuntimeException("sensitive details"));

        assertFailed(
                event,
                "password_reset_receiver_failed",
                "publicUserIdRef",
                "unavailable",
                "system_error");
    }

    private ResetPasswordCommand command() {
        return new ResetPasswordCommand("reset-token", "new-secure-password");
    }

    private void givenTokenPublicUserId(UUID publicUserId) {
        when(tokenService.decode("reset-token", passwordProperties))
                .thenReturn(new TokenPayload(publicUserId, Instant.EPOCH));
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
