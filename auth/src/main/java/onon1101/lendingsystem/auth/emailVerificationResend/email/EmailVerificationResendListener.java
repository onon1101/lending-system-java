package onon1101.lendingsystem.auth.emailVerificationResend.email;

import onon1101.lendingsystem.configurations.emailverification.EmailVerificationMailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailVerificationResendListener {

    private final EmailVerificationMailService mailService;

    public EmailVerificationResendListener(EmailVerificationMailService mailService) {
        this.mailService = mailService;
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EmailVerificationResendRequested event) {
        mailService.send(event.email(), event.username(), event.token());
    }
}
