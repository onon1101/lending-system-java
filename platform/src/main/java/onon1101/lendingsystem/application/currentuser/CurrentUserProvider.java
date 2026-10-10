package onon1101.lendingsystem.application.currentuser;

import reactor.core.publisher.Mono;

public interface CurrentUserProvider {

    Mono<CurrentUserContext> getCurrentUser();
}
