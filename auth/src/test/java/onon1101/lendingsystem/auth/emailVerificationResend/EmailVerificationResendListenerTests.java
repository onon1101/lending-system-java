package onon1101.lendingsystem.auth.emailVerificationResend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendListener;
import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendRequested;
import onon1101.lendingsystem.configurations.emailverification.EmailVerificationMailService;
import org.junit.jupiter.api.Test;

class EmailVerificationResendListenerTests {

    @Test
    void sendsVerificationEmail() {
        EmailVerificationMailService mailService = mock(EmailVerificationMailService.class);
        EmailVerificationResendListener listener =
                new EmailVerificationResendListener(mailService);

        listener.handle(
                new EmailVerificationResendRequested(
                        "alice@example.com", "alice", "email-token"));

        verify(mailService).send("alice@example.com", "alice", "email-token");
    }
}
