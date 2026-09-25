package onon1101.lendingsystem.auth.login.token;

import java.time.Duration;
import reactor.core.publisher.Mono;

public interface RefreshTokenStore {

    Mono<Void> save(String tokenHash, RefreshTokenSession session, Duration expiration);

    Mono<RefreshTokenSession> find(String tokenHash);

    Mono<Void> delete(String tokenHash);

    Mono<RefreshTokenSession> consume(String tokenHash);
}
