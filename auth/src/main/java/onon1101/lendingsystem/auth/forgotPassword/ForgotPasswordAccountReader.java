package onon1101.lendingsystem.auth.forgotPassword;

import reactor.core.publisher.Mono;

public interface ForgotPasswordAccountReader {
    Mono<ForgotPasswordAccount> findByEmail(String email);
}
