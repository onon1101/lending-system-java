package onon1101.lendingsystem.auth.login;

import java.time.Instant;
import java.util.UUID;

public record LoginAccount(
        UUID publicUserId,
        String username,
        String passwordHash,
        Integer passwordId,
        Instant lockedUntil) {}
