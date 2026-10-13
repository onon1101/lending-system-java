package onon1101.lendingsystem.order.borrow;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface BorrowOrderReader {

    Mono<OrderItem> lockItemForOrder(UUID orderId, long lenderId);

    Mono<LockedOrder> findOrderForUpdate(UUID orderId, long lenderId, long itemId);

    record OrderItem(long id, long ownerId, String status) {}

    record LockedOrder(
            long id, String status, Instant requestedAt, Instant updatedAt, Instant dueAt) {}
}
