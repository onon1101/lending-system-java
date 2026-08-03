package onon1101.lendingsystem.auth.forgotPassword;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.auth.forgotPassword.email.PasswordResetEmailRequested;
import onon1101.lendingsystem.auth.token.ForgotPasswordTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class ForgotPasswordServiceTests {

    private final ForgotPasswordAccountReader accountReader =
            mock(ForgotPasswordAccountReader.class);
    private final ForgotPasswordTokenService tokenService = mock(ForgotPasswordTokenService.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final ForgotPasswordService service =
            new ForgotPasswordService(accountReader, tokenService, eventPublisher);

    @Test
    void publishesPasswordResetEmailEventForExistingAccount() {
        UUID publicUserId = UUID.randomUUID();
        when(accountReader.findByEmail("user@example.com"))
                .thenReturn(Optional.of(new ForgotPasswordAccount(publicUserId, "test-user")));
        when(tokenService.createToken(publicUserId, "test-user")).thenReturn("reset-token");

        service.handle(" User@Example.com ");

        verify(eventPublisher)
                .publishEvent(
                        new PasswordResetEmailRequested(
                                "user@example.com", "test-user", "reset-token"));
    }

    @Test
    void doesNotPublishEventWhenAccountDoesNotExist() {
        when(accountReader.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        service.handle("missing@example.com");

        verify(eventPublisher, never()).publishEvent(any());
    }
}
