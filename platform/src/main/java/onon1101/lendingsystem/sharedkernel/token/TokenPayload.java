package onon1101.lendingsystem.sharedkernel.token;

import java.time.Instant;
import java.util.UUID;

public record TokenPayload(UUID publicUserId, Instant issuedAt) {}
