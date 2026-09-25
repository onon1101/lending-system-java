package onon1101.lendingsystem.item.retrieve;

import java.util.UUID;
import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;
import onon1101.lendingsystem.configurations.domain.Result;
import onon1101.lendingsystem.item.retrieve.error.ItemNotFoundDomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class RetrieveItemService {

    private final RetrieveItemReader itemReader;
    private final CurrentUserProvider currentUserProvider;

    public RetrieveItemService(
            RetrieveItemReader itemReader, CurrentUserProvider currentUserProvider) {
        this.itemReader = itemReader;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public Mono<Result<RetrieveItemResult>> retrieve(UUID itemId) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser ->
                                itemReader.findVisibleItem(itemId, currentUser.privateUserId()))
                .map(item -> Result.success(RetrieveItemResult.from(item)))
                .switchIfEmpty(
                        Mono.fromSupplier(() -> Result.failure(new ItemNotFoundDomainError())));
    }
}
