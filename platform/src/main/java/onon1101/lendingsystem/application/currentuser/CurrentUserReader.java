package onon1101.lendingsystem.application.currentuser;

import reactor.core.publisher.Mono;

public interface CurrentUserReader {

    Mono<CurrentUserContext> findByPrivateId(long privateUserId);
}
