package onon1101.lendingsystem.order.reject;

import reactor.core.publisher.Mono;

import java.time.Instant;

public interface RejectOrderWriter {

    Mono<Boolean> reject(
            long orderId,
            long lenderId,
            long itemId,
            Instant closedAt
    );
}
