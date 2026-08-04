package onon1101.lendingsystem.user.register.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
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

        assertEquals(new RegistrationAuditEvent.Succeeded("alice", userId), event);
    }

    @Test
    void mapsKnownBusinessFailure() {
        Object event =
                policy.onReturned(command("Alice"), Result.failure(new InvalidEmailDomainError()));

        assertEquals(new RegistrationAuditEvent.Failed("alice", "User.InvalidEmail"), event);
    }

    @Test
    void mapsUnexpectedExceptionWithoutExposingItsMessage() {
        Object event = policy.onThrown(command("Alice"), new RuntimeException("sensitive details"));

        assertEquals(new RegistrationAuditEvent.Failed("alice", "system_error"), event);
    }

    private RegisterCommand command(String username) {
        return new RegisterCommand(username, "password", "alice@example.com");
    }
}
