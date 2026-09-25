package onon1101.lendingsystem.auth.login;

import java.time.Instant;
import reactor.core.publisher.Mono;

// todo: 強制使用 IClock，並且使用 UTC
public interface LoginAccountWriter {
    Mono<FailedAttemptResult> recordFailedAttempt(
            Long passwordId, int maxAttempts, Instant lockedUntil);

    Mono<Void> resetFailedAttempts(Long passwordId);
}
