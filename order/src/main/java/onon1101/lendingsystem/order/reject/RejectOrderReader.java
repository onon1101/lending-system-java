package onon1101.lendingsystem.order.reject;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface RejectOrderReader {


    Mono<OrderItem> lockItemForOrder(UUID orderId, long lenderId);

    Mono<LockedOrder> findOrderForUpdate(
            UUID orderId,
            long lenderId,
            long itemId
    );

    record OrderItem(
            long id,
            long ownerId
    ) {
    }

    record LockedOrder(
            long id,
            String status,
            Instant requestedAt,
            Instant updatedAt
    ) {
    }
}
