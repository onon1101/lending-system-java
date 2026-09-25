package onon1101.lendingsystem.configurations.context.user;

import reactor.core.publisher.Mono;

public interface CurrentUserProvider {

    Mono<CurrentUserContext> getCurrentUser();
}
