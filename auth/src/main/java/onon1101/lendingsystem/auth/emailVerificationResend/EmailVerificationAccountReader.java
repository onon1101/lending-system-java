package onon1101.lendingsystem.auth.emailVerificationResend;

import reactor.core.publisher.Mono;

public interface EmailVerificationAccountReader {

    Mono<EmailVerificationAccount> findPendingByEmail(String email);
}
