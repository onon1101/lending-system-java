package onon1101.lendingsystem.item.create;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.Instant;
import onon1101.lendingsystem.application.idempotency.IdempotencyService;
import onon1101.lendingsystem.application.audit.AuditedCommand;
import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.shared.time.IClock;
import onon1101.lendingsystem.item.create.audit.CreateItemAuditPolicy;
import onon1101.lendingsystem.item.domain.Item;
import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemId;
import onon1101.lendingsystem.item.domain.ItemName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CreateItemService {

    private final CreateItemWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IdempotencyService idempotencyService;
    private final IClock clock;

    public CreateItemService(
            CreateItemWriter itemWriter,
            CurrentUserProvider currentUserContext,
            IClock clock,
            IdempotencyService idempotencyService) {
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserContext;
        this.clock = clock;
        this.idempotencyService = idempotencyService;
    }

    @Transactional
    @AuditedCommand(CreateItemAuditPolicy.class)
    public Mono<CreateItemResult> create(CreateItemCommand command) {
        CraeteItemPayload payload = new CraeteItemPayload(command.name(), command.description());

        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUserContext ->
                                idempotencyService.execute(
                                        currentUserContext.publicUserId().toString(),
                                        "item.create",
                                        command.idempotencyKey(),
                                        payload,
                                        CreateItemResult.class,
                                        () -> createItem(currentUserContext, command)));
    }

    private Mono<CreateItemResult> createItem(
            CurrentUserContext currentUser, CreateItemCommand command) {

        Instant now = clock.now();

        Item item =
                Item.create(
                        ItemId.of(UuidCreator.getTimeOrderedEpoch()),
                        currentUser.privateUserId(),
                        ItemName.of(command.name()),
                        ItemDescription.of(command.description()),
                        now);

        return itemWriter.create(item).thenReturn(new CreateItemResult(item.id().value()));
    }

    private record CraeteItemPayload(String name, String description) {}
}
