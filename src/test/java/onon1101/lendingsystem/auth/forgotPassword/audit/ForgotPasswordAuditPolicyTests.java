package onon1101.lendingsystem.auth.forgotPassword.audit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordCommand;
import onon1101.lendingsystem.auth.forgotPassword.ForgotPasswordResult;
import onon1101.lendingsystem.auth.forgotPassword.error.InvalidEmailDomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.junit.jupiter.api.Test;

class ForgotPasswordAuditPolicyTests {

    private final ForgotPasswordAuditPolicy policy = new ForgotPasswordAuditPolicy();

    @Test
    void producesRequestedEventForSuccessfulRequest() {
        Object event =
                policy.onReturned(
                        new ForgotPasswordCommand(" User@Example.com "),
                        Result.success(new ForgotPasswordResult()));

        assertThat(event).isEqualTo(new ForgotPasswordAuditEvent.Requested("user@example.com"));
    }

    @Test
    void producesRejectedEventForInvalidEmail() {
        Object event =
                policy.onReturned(
                        new ForgotPasswordCommand("invalid"),
                        Result.failure(new InvalidEmailDomainError()));

        assertThat(event)
                .isEqualTo(
                        new ForgotPasswordAuditEvent.Rejected(
                                "invalid", "ForgotPassword.InvalidEmail"));
    }

    @Test
    void producesFailedEventWhenCommandThrows() {
        Object event =
                policy.onThrown(
                        new ForgotPasswordCommand("User@Example.com"),
                        new RuntimeException("SMTP unavailable"));

        assertThat(event)
                .isEqualTo(new ForgotPasswordAuditEvent.Failed("user@example.com", "system_error"));
    }
}
