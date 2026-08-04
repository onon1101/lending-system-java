package onon1101.lendingsystem.user.register;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import onon1101.lendingsystem.user.register.email.EmailValidateListener;
import onon1101.lendingsystem.user.register.email.EmailValidateMailService;
import onon1101.lendingsystem.user.register.email.EmailValidateRequested;
import org.junit.jupiter.api.Test;

class EmailValidateListenerTests {
    @Test
    void sendsValidationEmail() {
        EmailValidateMailService mailService = mock(EmailValidateMailService.class);
        EmailValidateListener listener = new EmailValidateListener(mailService);

        listener.handle(new EmailValidateRequested("user@example.com", "alice", "email-token"));

        verify(mailService).sendEmailValidateEmail("user@example.com", "alice", "email-token");
    }
}
