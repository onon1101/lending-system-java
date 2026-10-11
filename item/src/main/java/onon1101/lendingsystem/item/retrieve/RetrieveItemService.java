package onon1101.lendingsystem.item.retrieve;

import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
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
    private final ObjectProvider<ImageStorage> imageStorageProvider;

    public RetrieveItemService(
            RetrieveItemReader itemReader,
            CurrentUserProvider currentUserProvider,
            ObjectProvider<ImageStorage> imageStorageProvider) {
        this.itemReader = itemReader;
        this.currentUserProvider = currentUserProvider;
        this.imageStorageProvider = imageStorageProvider;
    }

    @Transactional(readOnly = true)
    public Mono<Result<RetrieveItemResult>> retrieve(UUID itemId) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser ->
                                itemReader.findVisibleItem(itemId, currentUser.privateUserId()))
                .flatMap(this::toResult)
                .switchIfEmpty(
                        Mono.fromSupplier(() -> Result.failure(new ItemNotFoundDomainError())));
    }

    private Mono<Result<RetrieveItemResult>> toResult(RetrievedItem item) {
        ImageStorage storage = imageStorageProvider.getIfAvailable();
        String key = item.coverObjectKey();

        if (storage == null || key == null || key.isBlank()) {
            return Mono.just(Result.success(RetrieveItemResult.from(item)));
        }

        return storage.previewUrl(key)
                .map(url -> Result.<RetrieveItemResult>success(RetrieveItemResult.from(item, url)));
    }
}
