package onon1101.lendingsystem.item.delete;

import java.time.Instant;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.configurations.time.IClock;
import onon1101.lendingsystem.item.delete.error.ItemNotFoundDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class DeleteItemService {

    private final DeleteItemWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final IClock clock;

    public DeleteItemService(
            DeleteItemWriter itemWriter, CurrentUserProvider currentUserProvider, IClock clock) {
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.clock = clock;
    }

    @Transactional
    public Mono<Result<DeleteItemResult>> delete(DeleteItemCommand command) {
        Instant archivedAt = clock.now();
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser ->
                                itemWriter.archiveOwnedItem(
                                        command.itemId(), currentUser.privateUserId(), archivedAt))
                .map(
                        archived ->
                                archived
                                        ? Result.success(
                                                new DeleteItemResult(command.itemId(), archivedAt))
                                        : Result.failure(new ItemNotFoundDomainError()));
    }
}
