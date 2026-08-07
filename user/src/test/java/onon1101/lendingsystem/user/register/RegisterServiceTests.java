package onon1101.lendingsystem.user.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.sharedkernel.domain.result.Result;
import onon1101.lendingsystem.sharedkernel.token.emailvalidation.EmailValidateTokenService;
import onon1101.lendingsystem.user.register.email.EmailValidateRequested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTests {

    private final RegisterAccountWriter accountWriter = mock(RegisterAccountWriter.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final EmailValidateTokenService tokenService = mock(EmailValidateTokenService.class);
    private final RegisterService service =
            new RegisterService(accountWriter, passwordEncoder, eventPublisher, tokenService);

    @Test
    void registersNormalizedAccount() {
        UUID userId = UUID.randomUUID();
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(accountWriter.registerAccount("alice", "encoded", "alice@example.com"))
                .thenReturn(Optional.of(new RegisterAccount(1L, userId)));
        when(tokenService.createToken(userId, "alice")).thenReturn("email-token");

        Result<RegisterResult> result =
                service.register(new RegisterCommand(" Alice ", "password", "Alice@example.com"));

        RegisterResult registration = ((Result.Success<RegisterResult>) result).value();
        assertEquals(userId, registration.userId());
        verify(eventPublisher)
                .publishEvent(
                        new EmailValidateRequested("alice@example.com", "alice", "email-token"));
    }

    @Test
    void rejectsInvalidEmailBeforeEncodingPassword() {
        Result<RegisterResult> result =
                service.register(new RegisterCommand("Alice", "password", "not-an-email"));

        Result.Failure<RegisterResult> failure = (Result.Failure<RegisterResult>) result;
        assertEquals("User.InvalidEmail", failure.error().code());
        verify(passwordEncoder, never()).encode("password");
    }

    @Test
    void returnsFailureWhenAccountConflicts() {
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(accountWriter.registerAccount("alice", "encoded", "alice@example.com"))
                .thenReturn(Optional.empty());

        Result<RegisterResult> result =
                service.register(new RegisterCommand("Alice", "password", "alice@example.com"));

        Result.Failure<RegisterResult> failure = (Result.Failure<RegisterResult>) result;
        assertEquals("User.InvalidRegistration", failure.error().code());
    }

    @Test
    void propagatesUnexpectedFailure() {
        RuntimeException expected = new RuntimeException("encoder unavailable");
        when(passwordEncoder.encode("password")).thenThrow(expected);

        RuntimeException actual =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                service.register(
                                        new RegisterCommand(
                                                "Alice", "password", "alice@example.com")));

        assertSame(expected, actual);
    }
}
