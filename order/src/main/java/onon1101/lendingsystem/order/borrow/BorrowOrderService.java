package onon1101.lendingsystem.order.borrow;

import java.time.Instant;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.order.borrow.error.BorrowOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class BorrowOrderService {
    private final BorrowOrderReader orderReader;
    private final BorrowOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public BorrowOrderService(
            BorrowOrderReader orderReader,
            BorrowOrderWriter orderWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<BorrowOrderResult>> borrow(BorrowOrderCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> borrowForActor(command, user.privateUserId()));
    }

    private Mono<Result<BorrowOrderResult>> borrowForActor(
            BorrowOrderCommand command, long actorId) {
        return orderReader
                .lockItemForOrder(command.orderId(), actorId)
                .flatMap(
                        item -> {
                            if (item.ownerId() != actorId) {
                                return failure(BorrowOrderDomainError.notFound());
                            }
                            return orderReader
                                    .findOrderForUpdate(command.orderId(), actorId, item.id())
                                    .flatMap(
                                            order ->
                                                    borrowLockedOrder(
                                                            command, actorId, item, order));
                        })
                .switchIfEmpty(Mono.defer(() -> failure(BorrowOrderDomainError.notFound())));
    }

    private Mono<Result<BorrowOrderResult>> borrowLockedOrder(
            BorrowOrderCommand command,
            long actorId,
            BorrowOrderReader.OrderItem item,
            BorrowOrderReader.LockedOrder order) {
        if (!"approved".equals(order.status())) {
            return failure(BorrowOrderDomainError.invalidStatus());
        }
        Instant occurredAt = transitionTimeFor(order);
        if (!"available".equals(item.status())) {
            return failure(BorrowOrderDomainError.itemUnavailable());
        }
        if (order.dueAt() == null || !order.dueAt().isAfter(occurredAt)) {
            return failure(BorrowOrderDomainError.dueAtExpired());
        }
        return orderWriter
                .borrow(order.id(), actorId, item.id(), occurredAt)
                .map(
                        updated -> {
                            if (!updated) {
                                return Result.<BorrowOrderResult>failure(
                                        BorrowOrderDomainError.invalidStatus());
                            }
                            return Result.success(
                                    new BorrowOrderResult(
                                            command.orderId(), "borrowed", occurredAt));
                        });
    }

    private Instant transitionTimeFor(BorrowOrderReader.LockedOrder order) {
        Instant occurredAt = clock.now();
        if (occurredAt.isBefore(order.requestedAt())) {
            occurredAt = order.requestedAt();
        }
        if (occurredAt.isBefore(order.updatedAt())) {
            occurredAt = order.updatedAt();
        }
        return occurredAt;
    }

    private Mono<Result<BorrowOrderResult>> failure(BorrowOrderDomainError error) {
        return Mono.just(Result.failure(error));
    }
}
