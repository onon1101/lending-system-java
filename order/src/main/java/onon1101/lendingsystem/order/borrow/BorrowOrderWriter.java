package onon1101.lendingsystem.order.borrow;

import java.time.Instant;
import reactor.core.publisher.Mono;

public interface BorrowOrderWriter {

    Mono<Boolean> borrow(long orderId, long lenderId, long itemId, Instant borrowedAt);
}
