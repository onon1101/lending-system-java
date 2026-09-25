package onon1101.lendingsystem.configurations.Idempotency;

import reactor.core.publisher.Mono;

public interface IdempotencyRepository {

    Mono<Boolean> tryAcquire(String actorId, String operation, String key, String requestHash);

    Mono<IdempotencyRecord> find(String actorId, String operation, String key);

    Mono<Void> complete(String actorId, String operation, String key, Object response);
}
