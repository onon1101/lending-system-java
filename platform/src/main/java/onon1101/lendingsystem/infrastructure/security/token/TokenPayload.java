package onon1101.lendingsystem.infrastructure.security.token;

import java.time.Instant;
import java.util.UUID;

public record TokenPayload(UUID publicUserId, Instant issuedAt) {}
