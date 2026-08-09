package onon1101.lendingsystem.auth.emailVerificationResend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendRequested;
import onon1101.lendingsystem.auth.emailVerificationResend.redis.EmailVerificationResendThrottle;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class EmailVerificationResendServiceTests {

    private final EmailVerificationAccountReader accountReader =
            mock(EmailVerificationAccountReader.class);
    private final EmailVerificationResendThrottle throttle =
            mock(EmailVerificationResendThrottle.class);
    private final EmailValidateTokenService tokenService = mock(EmailValidateTokenService.class);
    private final ApplicationEventPublisher eventPublisher =
            mock(ApplicationEventPublisher.class);
    private final EmailVerificationResendService service =
            new EmailVerificationResendService(
                    accountReader, throttle, tokenService, eventPublisher);

    @Test
    void publishesEmailForPendingAccountOutsideCooldown() {
        UUID publicUserId = UUID.randomUUID();
        EmailVerificationAccount account =
                new EmailVerificationAccount(publicUserId, "alice", "alice@example.com");
        when(accountReader.findPendingByEmail("alice@example.com"))
                .thenReturn(Optional.of(account));
        when(throttle.acquire(publicUserId)).thenReturn(true);
        when(tokenService.createToken(publicUserId, "alice")).thenReturn("email-token");

        service.resend(new ResendEmailVerificationCommand(" Alice@Example.com "));

        verify(eventPublisher)
                .publishEvent(
                        new EmailVerificationResendRequested(
                                "alice@example.com", "alice", "email-token"));
    }

    @Test
    void returnsGenericSuccessWithoutPublishingWhenAccountDoesNotExist() {
        when(accountReader.findPendingByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        service.resend(new ResendEmailVerificationCommand("missing@example.com"));

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void returnsGenericSuccessWithoutPublishingDuringCooldown() {
        UUID publicUserId = UUID.randomUUID();
        EmailVerificationAccount account =
                new EmailVerificationAccount(publicUserId, "alice", "alice@example.com");
        when(accountReader.findPendingByEmail("alice@example.com"))
                .thenReturn(Optional.of(account));
        when(throttle.acquire(publicUserId)).thenReturn(false);

        service.resend(new ResendEmailVerificationCommand("alice@example.com"));

        verify(tokenService, never())
                .createToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(eventPublisher);
    }
}
