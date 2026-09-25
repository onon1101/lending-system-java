package onon1101.lendingsystem.configurations.context.user;

import reactor.core.publisher.Mono;

public interface UserCache {

    Mono<CurrentUserContext> find(long privateUserId);

    Mono<Void> save(CurrentUserContext user);

    Mono<Void> evict(long privateUserId);
}
