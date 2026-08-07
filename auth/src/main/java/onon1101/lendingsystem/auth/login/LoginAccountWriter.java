package onon1101.lendingsystem.auth.login;

import java.time.Instant;

// todo: 強制使用 IClock，並且使用 UTC
public interface LoginAccountWriter {
    FailedAttemptResult recordFailedAttempt(
            Integer passwordId, int maxAttempts, Instant lockedUntil);

    void resetFailedAttempts(Integer passwordId);
}
