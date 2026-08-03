package onon1101.lendingsystem.auth.login;

import java.time.Instant;

public interface LoginAccountWriter {
    FailedAttemptResult recordFailedAttempt(
            Integer passwordId,
            int maxAttempts,
            Instant lockedUntil
    );

    void resetFailedAttempts(Integer passwordId);
}
