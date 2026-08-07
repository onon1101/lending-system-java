package onon1101.lendingsystem.auth.login.token;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenSession(
        long privateUserId,
        UUID publicUserId,
        String username,
        Instant issuedAt
) {
}
