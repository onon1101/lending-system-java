package onon1101.lendingsystem.auth.login.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onon1101.lendingsystem.auth.login.LoginResult;
import onon1101.lendingsystem.sharedkernel.domain.result.DomainError;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import org.junit.jupiter.api.Test;

class AuthenticationAuditPolicyTests {

    private final AuthenticationAuditPolicy policy = new AuthenticationAuditPolicy();

    @Test
    void mapsSuccessfulResult() {
        Object event =
                policy.onReturned(
                        arguments(" Alice "), Result.success(new LoginResult("token", 300)));

        assertEquals(new AuthenticationAuditEvent.Succeeded("alice"), event);
    }

    @Test
    void mapsFailedResult() {
        Object event =
                policy.onReturned(
                        arguments("Alice"),
                        Result.failure(new DomainError("Auth.InvalidCredentials", "Invalid")));

        assertEquals(
                new AuthenticationAuditEvent.Failed("alice", "invalid_credentials"), event);
    }

    @Test
    void mapsTooManyAttemptsFailure() {
        Object event =
                policy.onReturned(
                        arguments("Alice"),
                        Result.failure(new DomainError("Auth.TooManyAttempts", "Locked")));

        assertEquals(
                new AuthenticationAuditEvent.Failed("alice", "too_many_attempts"), event);
    }

    @Test
    void mapsUnexpectedException() {
        Object event = policy.onThrown(arguments("Alice"), new RuntimeException("failure"));

        assertEquals(new AuthenticationAuditEvent.Failed("alice", "system_error"), event);
    }

    private Object[] arguments(String username) {
        return new Object[] {username, "password"};
    }
}
