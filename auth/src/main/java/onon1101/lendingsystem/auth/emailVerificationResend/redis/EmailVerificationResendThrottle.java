package onon1101.lendingsystem.auth.emailVerificationResend.redis;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface EmailVerificationResendThrottle {

    Mono<Boolean> acquire(UUID publicUserId);
}
