package onon1101.lendingsystem.order.create;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public interface CreateOrderWriter {

    Mono<Boolean> create(NewOrder order);

    record NewOrder (
            UUID orderId,
            long itemId,
            long borrowerId,
            long lenderId,
            String itemNameSnapshot,
            LocalDate plannedBorrowDate,
            LocalDate plannedReturnDate,
            Instant requestedAt
    ) {}
}
