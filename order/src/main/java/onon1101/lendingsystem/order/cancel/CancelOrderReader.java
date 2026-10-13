package onon1101.lendingsystem.order.cancel;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface CancelOrderReader {

    Mono<OrderItem> lockItemForOrder(UUID orderId, long borrowerId);

    Mono<LockedOrder> findOrderForUpdate(UUID orderId, long borrowerId, long itemId);

    record OrderItem(long id, long ownerId, String status) {}

    record LockedOrder(
            long id, String status, Instant requestedAt, Instant updatedAt, Instant dueAt) {}
}
