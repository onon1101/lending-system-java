package onon1101.lendingsystem.configurations.Idempotency;

import java.util.Optional;

public interface IdempotencyRepository {

  boolean tryAcquire(String actorId, String operation, String key,
                     String requestHash);

  Optional<IdempotencyRecord> find(String actorId, String operation,
                                   String key);

  void complete(String actorId, String operation, String key, Object response);
}
