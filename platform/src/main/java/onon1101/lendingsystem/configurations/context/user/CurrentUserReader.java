package onon1101.lendingsystem.configurations.context.user;

import reactor.core.publisher.Mono;

public interface CurrentUserReader {

    Mono<CurrentUserContext> findByPrivateId(long privateUserId);
}
