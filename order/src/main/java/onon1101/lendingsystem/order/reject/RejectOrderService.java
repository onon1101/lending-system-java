package onon1101.lendingsystem.order.reject;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.order.reject.error.RejectOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class RejectOrderService {

    private static final String PENDING = "pending";
    private static final String REJECTED = "rejected";

    private final RejectOrderReader orderReader;
    private final RejectOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public RejectOrderService(
            RejectOrderReader orderReader,
            RejectOrderWriter orderWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock
    ) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<RejectOrderResult>> reject(
            RejectOrderCommand command
    ) {
        return currentUserProvider.getCurrentUser()
                .flatMap(user ->
                        rejectForLender(command, user.privateUserId())
                );
    }

    private Mono<Result<RejectOrderResult>> rejectForLender(
            RejectOrderCommand command,
            long lenderId
    ) {
        return orderReader.lockItemForOrder(command.orderId(), lenderId)
                .flatMap(item -> {
                    if (item.ownerId() != lenderId) {
                        return failure(RejectOrderDomainError.notFound());
                    }

                    return rejectForItem(command, lenderId, item.id());
                })
                .switchIfEmpty(Mono.defer(() ->
                        failure(RejectOrderDomainError.notFound())
                ));
    }

    private Mono<Result<RejectOrderResult>> rejectForItem(
            RejectOrderCommand command,
            long lenderId,
            long itemId
    ) {
        return orderReader.findOrderForUpdate(
                        command.orderId(),
                        lenderId,
                        itemId
                )
                .flatMap(order ->
                        rejectLockedOrder(command, lenderId, itemId, order)
                );
    }

    private Mono<Result<RejectOrderResult>> rejectLockedOrder(
            RejectOrderCommand command,
            long lenderId,
            long itemId,
            RejectOrderReader.LockedOrder order
    ) {
        if (!PENDING.equals(order.status())) {
            return failure(RejectOrderDomainError.invalidStatus());
        }

        Instant closedAt = closingTimeFor(order);

        return orderWriter.reject(
                        order.id(),
                        lenderId,
                        itemId,
                        closedAt
                )
                .map(updated -> {
                    if (!updated) {
                        return Result.<RejectOrderResult>failure(
                                RejectOrderDomainError.invalidStatus()
                        );
                    }

                    RejectOrderResult result = new RejectOrderResult(
                            command.orderId(),
                            REJECTED,
                            closedAt
                    );

                    return Result.success(result);
                });
    }

    private Instant closingTimeFor(
            RejectOrderReader.LockedOrder order
    ) {
        Instant closedAt = clock.now();

        if (closedAt.isBefore(order.requestedAt())) {
            closedAt = order.requestedAt();
        }

        if (closedAt.isBefore(order.updatedAt())) {
            closedAt = order.updatedAt();
        }

        return closedAt;
    }

    private Mono<Result<RejectOrderResult>> failure(
            RejectOrderDomainError error
    ) {
        return Mono.just(Result.failure(error));
    }
}
