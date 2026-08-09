package onon1101.lendingsystem.item.create;

import com.github.f4b6a3.uuid.UuidCreator;
import onon1101.lendingsystem.configurations.audit.AuditedCommand;
import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;

import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;

import onon1101.lendingsystem.item.create.audit.CreateItemAuditPolicy;
import onon1101.lendingsystem.item.domain.Item;

import onon1101.lendingsystem.item.domain.ItemDescription;
import onon1101.lendingsystem.item.domain.ItemId;

import onon1101.lendingsystem.item.domain.ItemName;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateItemService {

    private final CreateItemWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public CreateItemService(
            CreateItemWriter itemWriter,
            CurrentUserProvider currentUserContext,
            IClock clock
    ) {
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserContext;
        this.clock = clock;
    }

    @Transactional
    @AuditedCommand(CreateItemAuditPolicy.class)
    public CreateItemResult create(CreateItemCommand command) {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();
        Instant now = clock.now();

        Item item = Item.create(
                ItemId.of(UuidCreator.getTimeOrderedEpoch()),
                currentUser.privateUserId(),
                ItemName.of(command.name()),
                ItemDescription.of(command.description()),
                now);

        itemWriter.create(item);

        return new CreateItemResult(item.id().value());
    }
}
