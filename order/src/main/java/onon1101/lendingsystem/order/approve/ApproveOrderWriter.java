package onon1101.lendingsystem.order.approve;

import reactor.core.publisher.Mono;

import java.time.Instant;

public interface ApproveOrderWriter {

    Mono<Boolean> hasActiveOrder(long itemId);

    Mono<Boolean> approve(
            long orderId,
            long lenderId,
            long itemId,
            Instant approvedAt,
            Instant dueAt
    );
}
