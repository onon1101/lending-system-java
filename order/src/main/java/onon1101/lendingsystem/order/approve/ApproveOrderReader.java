package onon1101.lendingsystem.order.approve;

import onon1101.lendingsystem.order.create.CreateOrderReader;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface ApproveOrderReader {

    Mono<OrderItem> lockItemForOrder(UUID orderId, long lenderId);

    Mono<PendingOrder> findOrderForUpdate(
            UUID orderId,
            long lenderId,
            long itemId
    );

    record OrderItem(
            long id,
            long ownerId,
            String status
    ) {
    }

    record PendingOrder(
            long id,
            String status,
            Instant requestedAt
    ) {
    }
}
