package onon1101.lendingsystem.order.returnOrder;

import java.time.Instant;
import reactor.core.publisher.Mono;

public interface ReturnOrderWriter {

    Mono<Boolean> returnOrder(long orderId, long lenderId, long itemId, Instant returnedAt);
}
