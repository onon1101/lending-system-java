package onon1101.lendingsystem.user.register;

import reactor.core.publisher.Mono;

public interface RegisterAccountWriter {
    Mono<RegisterAccount> registerAccount(String username, String password, String email);
}
