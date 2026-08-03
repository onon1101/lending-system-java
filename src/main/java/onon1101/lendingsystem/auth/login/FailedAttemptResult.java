package onon1101.lendingsystem.auth.login;

import java.time.Instant;

public record FailedAttemptResult(
        int failedAttempts,
        Instant lockedUntil
) {
    public boolean locked() {
        return lockedUntil != null;
    }
}
