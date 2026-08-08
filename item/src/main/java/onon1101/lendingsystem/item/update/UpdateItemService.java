package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;

import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;

import onon1101.lendingsystem.item.domain.Item;

import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemName;
import onon1101.lendingsystem.item.update.error.ItemNotFoundDomainError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
            IClock clock
    ) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Result<UpdateItemResult> update(UpdateItemCommand command) {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();

        Item item =
                itemReader
                        .finOwnedItem(
                                command.itemId(),
                                currentUser.privateUserId())
                        .orElse(null);

        if (item == null) {
            return Result.failure(new ItemNotFoundDomainError());
        }

        Instant now = clock.now();

        item.updateDetails(ItemName.of(command.name()),
                ItemDescription.of(command.description()),
                now);

        if (!itemWriter.update(item)) {
            return Result.failure(new ItemNotFoundDomainError());
        }

        return Result.success(
                new UpdateItemResult(
                        item.id().value(),
                        item.name().value(),
                        item.description().value(),
                        item.updatedAt()
                )
        );
    }
}
