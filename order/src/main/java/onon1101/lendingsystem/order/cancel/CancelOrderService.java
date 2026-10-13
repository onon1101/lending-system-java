package onon1101.lendingsystem.order.cancel;

import java.time.Instant;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.order.cancel.error.CancelOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CancelOrderService {
    private final CancelOrderReader orderReader;
    private final CancelOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public CancelOrderService(
            CancelOrderReader orderReader,
            CancelOrderWriter orderWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<CancelOrderResult>> cancel(CancelOrderCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> cancelForActor(command, user.privateUserId()));
    }

    private Mono<Result<CancelOrderResult>> cancelForActor(
            CancelOrderCommand command, long actorId) {
        return orderReader
                .lockItemForOrder(command.orderId(), actorId)
                .flatMap(
                        item -> {
                            return orderReader
                                    .findOrderForUpdate(command.orderId(), actorId, item.id())
                                    .flatMap(
                                            order ->
                                                    cancelLockedOrder(
                                                            command, actorId, item, order));
                        })
                .switchIfEmpty(Mono.defer(() -> failure(CancelOrderDomainError.notFound())));
    }

    private Mono<Result<CancelOrderResult>> cancelLockedOrder(
            CancelOrderCommand command,
            long actorId,
            CancelOrderReader.OrderItem item,
            CancelOrderReader.LockedOrder order) {
        if (!"pending".equals(order.status()) && !"approved".equals(order.status())) {
            return failure(CancelOrderDomainError.invalidStatus());
        }
        Instant occurredAt = transitionTimeFor(order);

        return orderWriter
                .cancel(order.id(), actorId, item.id(), occurredAt)
                .map(
                        updated -> {
                            if (!updated) {
                                return Result.<CancelOrderResult>failure(
                                        CancelOrderDomainError.invalidStatus());
                            }
                            return Result.success(
                                    new CancelOrderResult(
                                            command.orderId(), "cancelled", occurredAt));
                        });
    }

    private Instant transitionTimeFor(CancelOrderReader.LockedOrder order) {
        Instant occurredAt = clock.now();
        if (occurredAt.isBefore(order.requestedAt())) {
            occurredAt = order.requestedAt();
        }
        if (occurredAt.isBefore(order.updatedAt())) {
            occurredAt = order.updatedAt();
        }
        return occurredAt;
    }

    private Mono<Result<CancelOrderResult>> failure(CancelOrderDomainError error) {
        return Mono.just(Result.failure(error));
    }
}
