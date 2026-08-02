package onon1101.lendingsystem.auth.forgotPassword;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** Handles password-reset email delivery outside the request thread. */
@Component
public class PasswordResetEmailListener {

    private final PasswordResetMailService mailService;

    public PasswordResetEmailListener(PasswordResetMailService mailService) {
        this.mailService = mailService;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handle(PasswordResetEmailRequested event) {
        mailService.sendResetPasswordEmail(event.email(), event.username(), event.resetToken());
    }
}
