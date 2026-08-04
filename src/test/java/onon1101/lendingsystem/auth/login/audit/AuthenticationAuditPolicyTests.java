package onon1101.lendingsystem.auth.login.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onon1101.lendingsystem.auth.login.LoginCommand;
import onon1101.lendingsystem.auth.login.LoginResult;
import onon1101.lendingsystem.auth.login.error.InvalidCredentialsDomainError;
import onon1101.lendingsystem.auth.login.error.TooManyAttemptsDomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.junit.jupiter.api.Test;

class AuthenticationAuditPolicyTests {

    private final AuthenticationAuditPolicy policy = new AuthenticationAuditPolicy();

    @Test
    void mapsSuccessfulResult() {
        Object event =
                policy.onReturned(
                        command(" Alice "), Result.success(new LoginResult("token", 300)));

        assertEquals(new AuthenticationAuditEvent.Succeeded("alice"), event);
    }

    @Test
    void mapsFailedResult() {
        Object event =
                policy.onReturned(
                        command("Alice"), Result.failure(new InvalidCredentialsDomainError()));

        assertEquals(
                new AuthenticationAuditEvent.Failed("alice", "Auth.InvalidCredentials"), event);
    }

    @Test
    void mapsTooManyAttemptsFailure() {
        Object event =
                policy.onReturned(
                        command("Alice"), Result.failure(new TooManyAttemptsDomainError()));

        assertEquals(new AuthenticationAuditEvent.Failed("alice", "Auth.TooManyAttempts"), event);
    }

    @Test
    void mapsUnexpectedException() {
        Object event = policy.onThrown(command("Alice"), new RuntimeException("failure"));

        assertEquals(new AuthenticationAuditEvent.Failed("alice", "system_error"), event);
    }

    private LoginCommand command(String username) {
        return new LoginCommand(username, "password");
    }
}
