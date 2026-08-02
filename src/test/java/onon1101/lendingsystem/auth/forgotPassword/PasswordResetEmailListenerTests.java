package onon1101.lendingsystem.auth.forgotPassword;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

class PasswordResetEmailListenerTests {

    @Test
    void delegatesEventToMailService() {
        PasswordResetMailService mailService = mock(PasswordResetMailService.class);
        PasswordResetEmailListener listener = new PasswordResetEmailListener(mailService);

        listener.handle(
                new PasswordResetEmailRequested("user@example.com", "test-user", "reset-token"));

        verify(mailService).sendResetPasswordEmail("user@example.com", "test-user", "reset-token");
    }
}
