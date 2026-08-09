package onon1101.lendingsystem.user.register;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import onon1101.lendingsystem.configurations.email.EmailVerificationMailService;
import onon1101.lendingsystem.user.register.email.EmailValidateListener;
import onon1101.lendingsystem.user.register.email.EmailValidateRequested;
import org.junit.jupiter.api.Test;

class EmailValidateListenerTests {
    @Test
    void sendsValidationEmail() {
        EmailVerificationMailService mailService = mock(EmailVerificationMailService.class);
        EmailValidateListener listener = new EmailValidateListener(mailService);

        listener.handle(new EmailValidateRequested("user@example.com", "alice", "email-token"));

        verify(mailService).send("user@example.com", "alice", "email-token");
    }
}
