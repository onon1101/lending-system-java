package onon1101.lendingsystem.order.cancel;

import java.time.Instant;
import reactor.core.publisher.Mono;

public interface CancelOrderWriter {

    Mono<Boolean> cancel(long orderId, long borrowerId, long itemId, Instant closedAt);
}
