package onon1101.lendingsystem.auth.resetPassword;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ResetPasswordWriter {

    Mono<Boolean> updatePassword(UUID publicUserId, String encodedPassword, Instant tokenIssuedAt);
}
