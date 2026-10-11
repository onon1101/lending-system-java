package onon1101.lendingsystem.create;

import com.github.f4b6a3.uuid.UuidCreator;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;

import onon1101.lendingsystem.create.error.InvalidOrderDatesDomainError;
import onon1101.lendingsystem.create.error.OrderConflictDomainError;
import onon1101.lendingsystem.create.error.OrderItemNotFoundDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class CreateOrderService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Taipei");

    private final CreateOrderReader orderReader;
    private final CreateOrderWriter orderWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public CreateOrderService(CreateOrderReader orderReader,
                              CreateOrderWriter orderWriter,
                              CurrentUserProvider currentUserProvider,
                              IClock clock) {
        this.orderReader = orderReader;
        this.orderWriter = orderWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<CreateOrderResult>> create(CreateOrderCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(currentUser -> createOrder(currentUser, command));
    }

    private Mono<Result<CreateOrderResult>> createOrder(
            CurrentUserContext currentUser, CreateOrderCommand command) {
        Instant now = clock.now();
        LocalDate today = now
                .atZone(BUSINESS_ZONE)
                .toLocalDate();

        if (command
                .plannedBorrowDate()
                .isBefore(today)) {
            return Mono.just(Result.failure(new InvalidOrderDatesDomainError(
                    "Planned borrow date must not be before.")));
        }

        if (!command
                .plannedReturnDate()
                .isAfter(command.plannedBorrowDate())) {
            return Mono.just(Result.failure(new InvalidOrderDatesDomainError(
                    "Planned return date must be after planned.")));
        }

        return orderReader
                .findItemForUpdate(command.itemId())
                .flatMap(item -> createOrderForItem(currentUser, command, item,
                        now))
                .switchIfEmpty(Mono.fromSupplier(() -> Result.failure(
                        new OrderItemNotFoundDomainError())));
    }

    private Mono<Result<CreateOrderResult>> createOrderForItem(
            CurrentUserContext currentUser,
            CreateOrderCommand command,
            CreateOrderReader.OrderItem item,
            Instant now
    ) {

        if (item.ownerId() == currentUser.privateUserId()) {
            return Mono.just(Result.failure(
                    OrderConflictDomainError.cannotBorrowOwnItem()));
        }

        if (!item.acceptsRequests()) {
            return Mono.just(
                    Result.failure(OrderConflictDomainError.itemUnavailable()));
        }

        CreateOrderWriter.NewOrder newOrder = new CreateOrderWriter.NewOrder(
                UuidCreator.getTimeOrderedEpoch(),
                item.id(),
                currentUser.privateUserId(),
                item.ownerId(),
                item.name(),
                command.plannedBorrowDate(),
                command.plannedReturnDate(),
                now
        );

        return orderWriter
                .create(newOrder)
                .map(created -> {
                    if (!created) {
                        return Result.<CreateOrderResult>failure(
                                OrderConflictDomainError.openRequestExists()
                        );
                    }

                    return Result.success(
                            new CreateOrderResult(
                                    newOrder.orderId(),
                                    CreateOrderResult.Status.PENDING,
                                    newOrder.plannedBorrowDate(),
                                    newOrder.plannedReturnDate()
                            )
                    );
                });
    }
}
