package onon1101.lendingsystem.item.update;

import java.time.Instant;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;
import onon1101.lendingsystem.item.domain.Item;
import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemName;
import onon1101.lendingsystem.item.update.error.ItemNotFoundDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class UpdateItemService {

    private final UpdateItemReader itemReader;
    private final UpdateItemWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public UpdateItemService(
            UpdateItemReader itemReader,
            UpdateItemWriter itemWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<UpdateItemResult>> update(UpdateItemCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser ->
                                itemReader.finOwnedItem(
                                        command.itemId(), currentUser.privateUserId()))
                .flatMap(item -> updateItem(item, command))
                .switchIfEmpty(
                        Mono.fromSupplier(() -> Result.failure(new ItemNotFoundDomainError())));
    }

    private Mono<Result<UpdateItemResult>> updateItem(Item item, UpdateItemCommand command) {
        Instant now = clock.now();
        item.updateDetails(
                ItemName.of(command.name()), ItemDescription.of(command.description()), now);

        return itemWriter
                .update(item)
                .map(
                        updated ->
                                updated
                                        ? Result.success(
                                                new UpdateItemResult(
                                                        item.id().value(), item.name().value(),
                                                        item.description().value(),
                                                                item.updatedAt()))
                                        : Result.failure(new ItemNotFoundDomainError()));
    }
}
