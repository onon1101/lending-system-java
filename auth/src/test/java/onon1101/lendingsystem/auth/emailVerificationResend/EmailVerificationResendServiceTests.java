package onon1101.lendingsystem.auth.emailVerificationResend;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import onon1101.lendingsystem.auth.emailVerificationResend.email.EmailVerificationResendRequested;
import onon1101.lendingsystem.auth.emailVerificationResend.redis.EmailVerificationResendThrottle;
import onon1101.lendingsystem.configurations.token.emailvalidation.EmailValidateTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import reactor.core.publisher.Mono;

class EmailVerificationResendServiceTests {

    private final EmailVerificationAccountReader accountReader =
            mock(EmailVerificationAccountReader.class);
    private final EmailVerificationResendThrottle throttle =
            mock(EmailVerificationResendThrottle.class);
    private final EmailValidateTokenService tokenService = mock(EmailValidateTokenService.class);
    private final TransactionalEventPublisher eventPublisher =
            mock(TransactionalEventPublisher.class);
    private final EmailVerificationResendService service =
            new EmailVerificationResendService(
                    accountReader, throttle, tokenService, eventPublisher);

    @Test
    void publishesEmailForPendingAccountOutsideCooldown() {
        UUID publicUserId = UUID.randomUUID();
        EmailVerificationAccount account =
                new EmailVerificationAccount(publicUserId, "alice", "alice@example.com");
        when(accountReader.findPendingByEmail("alice@example.com")).thenReturn(Mono.just(account));
        when(throttle.acquire(publicUserId)).thenReturn(Mono.just(true));
        when(tokenService.createToken(publicUserId, "alice")).thenReturn("email-token");
        when(eventPublisher.publishEvent(
                        org.mockito.ArgumentMatchers.any(EmailVerificationResendRequested.class)))
                .thenReturn(Mono.empty());

        service.resend(new ResendEmailVerificationCommand(" Alice@Example.com ")).block();

        verify(eventPublisher)
                .publishEvent(
                        new EmailVerificationResendRequested(
                                "alice@example.com", "alice", "email-token"));
    }

    @Test
    void returnsGenericSuccessWithoutPublishingWhenAccountDoesNotExist() {
        when(accountReader.findPendingByEmail("missing@example.com")).thenReturn(Mono.empty());

        service.resend(new ResendEmailVerificationCommand("missing@example.com")).block();

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void returnsGenericSuccessWithoutPublishingDuringCooldown() {
        UUID publicUserId = UUID.randomUUID();
        EmailVerificationAccount account =
                new EmailVerificationAccount(publicUserId, "alice", "alice@example.com");
        when(accountReader.findPendingByEmail("alice@example.com")).thenReturn(Mono.just(account));
        when(throttle.acquire(publicUserId)).thenReturn(Mono.just(false));

        service.resend(new ResendEmailVerificationCommand("alice@example.com")).block();

        verify(tokenService, never())
                .createToken(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(eventPublisher);
    }
}
