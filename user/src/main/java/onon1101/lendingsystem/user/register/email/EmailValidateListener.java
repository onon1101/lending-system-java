package onon1101.lendingsystem.user.register.email;

import onon1101.lendingsystem.configurations.email.EmailVerificationMailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailValidateListener {
    private final EmailVerificationMailService mailService;

    public EmailValidateListener(EmailVerificationMailService mailService) {
        this.mailService = mailService;
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EmailValidateRequested event) {
        mailService.send(event.email(), event.username(), event.emailToken());
    }
}
