package onon1101.lendingsystem.item.delete;

import onon1101.lendingsystem.configurations.context.user.CurrentUserContext;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;

import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;

import onon1101.lendingsystem.item.delete.error.ItemNotFoundDomainError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DeleteItemService {

    private final DeleteItemWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public DeleteItemService(
            DeleteItemWriter itemWriter,
            CurrentUserProvider currentUserProvider,
            IClock clock
    ) {
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Result<DeleteItemResult> delete(DeleteItemCommand command) {
        CurrentUserContext currentUser = currentUserProvider.getCurrentUser();

        Instant archivedAt = clock.now();

        boolean archived = itemWriter.archiveOwnedItem(
                command.itemId(),
                currentUser.privateUserId(),
                archivedAt
        );

        if (!archived) {
            return Result.failure(
                    new ItemNotFoundDomainError()
            );
        }

        return Result.success(new DeleteItemResult(command.itemId(), archivedAt));
    }
}
