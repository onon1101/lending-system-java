package onon1101.lendingsystem.auth.login;

import java.time.Instant;
import java.util.UUID;

public record LoginAccount(
        long privateUserId,
        UUID publicUserId,
        String username,
        String passwordHash,
        String email,
        Integer passwordId,
        Instant lockedUntil) {}
