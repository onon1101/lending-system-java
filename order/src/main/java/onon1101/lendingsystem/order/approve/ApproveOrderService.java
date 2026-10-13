package onon1101.lendingsystem.order.approve;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;

import onon1101.lendingsystem.order.approve.error.ApproveOrderDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
public class ApproveOrderService {

    private static final String PENDING = "pending";
    private static final String APPROVED = "approved";
    private static final String AVAILABLE = "available";

    private final ApproveOrderReader orderReader;
    private final ApproveOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public ApproveOrderService(
            ApproveOrderReader orderReader,
            ApproveOrderWriter orderWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock
    ) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<ApproveOrderResult>> approve(
            ApproveOrderCommand command
    ) {

        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> approveForLender(command,
                        user.privateUserId()));
    }

    private Mono<Result<ApproveOrderResult>> approveForLender(
            ApproveOrderCommand command, long lenderId) {

        return orderReader
                .lockItemForOrder(command.orderId(), lenderId)
                .flatMap(item -> {
                    if (item.ownerId() != lenderId) {
                        return failure(ApproveOrderDomainError.notFound());
                    }

                    return approveForItem(command, lenderId, item);
                })
                .switchIfEmpty(Mono.defer(
                        () -> failure(ApproveOrderDomainError.notFound())));
    }

    private Mono<Result<ApproveOrderResult>> approveForItem(
            ApproveOrderCommand command,
            long lenderId,
            ApproveOrderReader.OrderItem item
    ) {
        return orderReader
                .findOrderForUpdate(
                        command.orderId(),
                        lenderId,
                        item.id()
                )
                .flatMap(order -> approveLockedOrder(
                        command,
                        lenderId,
                        item,
                        order
                ));
    }

    private Mono<Result<ApproveOrderResult>> approveLockedOrder(
            ApproveOrderCommand command,
            long lenderId,
            ApproveOrderReader.OrderItem item,
            ApproveOrderReader.PendingOrder order
    ) {

        if (!PENDING.equals(order.status())) {
            return failure(ApproveOrderDomainError.invalidStatus());
        }

        if (!AVAILABLE.equals(item.status())) {
            return failure(ApproveOrderDomainError.invalidStatus());
        }

        Instant approvedAt = clock.now();

        if (!isValidApprovalTime(
                order.requestedAt(),
                approvedAt,
                command.dueAt()
        )) {
            return failure(ApproveOrderDomainError.invalidDueAt());
        }

        return approveIfNotActiveOrder(
                command,
                lenderId,
                item.id(),
                order.id(),
                approvedAt
        );
    }

    private boolean isValidApprovalTime(
            Instant requestedAt,
            Instant approvedAt,
            Instant dueAt
    ) {
        return !approvedAt.isBefore(requestedAt) && approvedAt.isAfter(dueAt);
    }

    private Mono<Result<ApproveOrderResult>> approveIfNotActiveOrder(
            ApproveOrderCommand command,
            long lenderId,
            long itemId,
            long orderId,
            Instant approvedAt
    ) {

        return orderWriter
                .hasActiveOrder(itemId)
                .flatMap(activeOrderExists -> {
                    if (activeOrderExists) {
                        return failure(
                                ApproveOrderDomainError.activeOrderExists());
                    }

                    return persistApproval(
                            command,
                            lenderId,
                            itemId,
                            orderId,
                            approvedAt
                    );
                });
    }

    private Mono<Result<ApproveOrderResult>> persistApproval(
            ApproveOrderCommand command,
            long lenderId,
            long itemId,
            long orderId,
            Instant approvedAt
    ) {

        return orderWriter
                .approve(
                        orderId,
                        lenderId,
                        itemId,
                        approvedAt,
                        command.dueAt()
                )
                .map(updated -> {
                    if (!updated) {
                        return Result.failure(
                                ApproveOrderDomainError.invalidStatus());
                    }

                    ApproveOrderResult result = new ApproveOrderResult(
                            command.orderId(),
                            APPROVED,
                            approvedAt,
                            command.dueAt()
                    );

                    return Result.success(result);
                });
    }

    private Mono<Result<ApproveOrderResult>> failure(
            ApproveOrderDomainError error
    ) {
        return Mono.just(Result.failure(error));
    }
}
