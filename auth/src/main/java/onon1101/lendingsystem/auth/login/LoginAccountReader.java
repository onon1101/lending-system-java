package onon1101.lendingsystem.auth.login;

import reactor.core.publisher.Mono;

public interface LoginAccountReader {
    Mono<LoginAccount> findByUsername(String username);
}
