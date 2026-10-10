package onon1101.lendingsystem.user.register;

import reactor.core.publisher.Mono;

public interface RegisterAccountReader {

    Mono<Boolean> existsByUsernameOrEmail(String username, String email);
}
