package onon1101.lendingsystem.item.retrieve;

import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImagePreviewUrlResolver;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.item.retrieve.error.ItemNotFoundDomainError;
import onon1101.lendingsystem.shared.domain.Result;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class RetrieveItemService {

    private final RetrieveItemReader itemReader;
    private final CurrentUserProvider currentUserProvider;
    private final ImagePreviewUrlResolver imageResolver;

    public RetrieveItemService(
            RetrieveItemReader itemReader,
            CurrentUserProvider currentUserProvider,
            ImagePreviewUrlResolver imageResolver){
        this.itemReader = itemReader;
        this.currentUserProvider = currentUserProvider;
        this.imageResolver = imageResolver;
    }

    @Transactional(readOnly = true)
    public Mono<Result<RetrieveItemResult>> retrieve(UUID itemId) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser ->
                                itemReader.findVisibleItem(itemId, currentUser.privateUserId()))
                .flatMap(item ->
                    imageResolver.resolve(item.coverObjectKey())
                            .map(url -> Result.success(RetrieveItemResult.from(item, url)))
                )
                .switchIfEmpty(
                        Mono.fromSupplier(() -> Result.failure(new ItemNotFoundDomainError())));
    }
}
