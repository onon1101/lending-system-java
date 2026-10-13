package onon1101.lendingsystem.order.returnOrder;

import java.time.Instant;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.order.returnOrder.error.ReturnOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class ReturnOrderService {
    private final ReturnOrderReader orderReader;
    private final ReturnOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public ReturnOrderService(
            ReturnOrderReader orderReader,
            ReturnOrderWriter orderWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<ReturnOrderResult>> returnOrder(ReturnOrderCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> returnOrderForActor(command, user.privateUserId()));
    }

    private Mono<Result<ReturnOrderResult>> returnOrderForActor(
            ReturnOrderCommand command, long actorId) {
        return orderReader
                .lockItemForOrder(command.orderId(), actorId)
                .flatMap(
                        item -> {
                            if (item.ownerId() != actorId) {
                                return failure(ReturnOrderDomainError.notFound());
                            }
                            return orderReader
                                    .findOrderForUpdate(command.orderId(), actorId, item.id())
                                    .flatMap(
                                            order ->
                                                    returnOrderLockedOrder(
                                                            command, actorId, item, order));
                        })
                .switchIfEmpty(Mono.defer(() -> failure(ReturnOrderDomainError.notFound())));
    }

    private Mono<Result<ReturnOrderResult>> returnOrderLockedOrder(
            ReturnOrderCommand command,
            long actorId,
            ReturnOrderReader.OrderItem item,
            ReturnOrderReader.LockedOrder order) {
        if (!"borrowed".equals(order.status())) {
            return failure(ReturnOrderDomainError.invalidStatus());
        }
        Instant occurredAt = transitionTimeFor(order);

        return orderWriter
                .returnOrder(order.id(), actorId, item.id(), occurredAt)
                .map(
                        updated -> {
                            if (!updated) {
                                return Result.<ReturnOrderResult>failure(
                                        ReturnOrderDomainError.invalidStatus());
                            }
                            return Result.success(
                                    new ReturnOrderResult(
                                            command.orderId(), "returned", occurredAt));
                        });
    }

    private Instant transitionTimeFor(ReturnOrderReader.LockedOrder order) {
        Instant occurredAt = clock.now();
        if (occurredAt.isBefore(order.requestedAt())) {
            occurredAt = order.requestedAt();
        }
        if (occurredAt.isBefore(order.updatedAt())) {
            occurredAt = order.updatedAt();
        }
        return occurredAt;
    }

    private Mono<Result<ReturnOrderResult>> failure(ReturnOrderDomainError error) {
        return Mono.just(Result.failure(error));
    }
}
