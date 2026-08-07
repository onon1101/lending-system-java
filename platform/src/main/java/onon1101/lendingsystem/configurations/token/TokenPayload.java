package onon1101.lendingsystem.configurations.token;

import java.time.Instant;
import java.util.UUID;

public record TokenPayload(UUID publicUserId, Instant issuedAt) {}
