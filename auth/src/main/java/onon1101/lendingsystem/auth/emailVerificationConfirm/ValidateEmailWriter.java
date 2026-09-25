package onon1101.lendingsystem.auth.emailVerificationConfirm;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface ValidateEmailWriter {

    Mono<Boolean> updateStateByPublicId(UUID email);
}
