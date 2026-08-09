package onon1101.lendingsystem.user.register;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import onon1101.lendingsystem.configurations.email.EmailVerificationMailServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailValidateMailServiceTests {
    @Test
    void buildsValidationLinkAndSendsMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailVerificationMailServiceImpl service =
                new EmailVerificationMailServiceImpl(
                        mailSender, "no-reply@example.com", "https://example.com/validate-email");

        service.send("user@example.com", "alice", "signed.token");

        ArgumentCaptor<SimpleMailMessage> messageCaptor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("no-reply@example.com", message.getFrom());
        assertEquals("user@example.com", message.getTo()[0]);
        assertTrue(
                message.getText()
                        .contains("https://example.com/validate-email?token=signed.token"));
    }
}
