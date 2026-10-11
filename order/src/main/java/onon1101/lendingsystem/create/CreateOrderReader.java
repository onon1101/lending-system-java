package onon1101.lendingsystem.create;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CreateOrderReader {

    Mono<OrderItem> findItemForUpdate(UUID itemId);

    record OrderItem(
            long id,
            long ownerId,
            String name,
            boolean acceptsRequests
    ) {}
}
