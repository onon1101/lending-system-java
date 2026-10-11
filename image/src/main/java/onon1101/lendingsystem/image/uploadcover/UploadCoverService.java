package onon1101.lendingsystem.image.uploadcover;

import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.image.uploadcover.error.ItemCoverDomainError;
import onon1101.lendingsystem.image.uploadcover.error.ItemCoverValidationException;
import onon1101.lendingsystem.image.uploadcover.repository.UploadCoverReader;
import onon1101.lendingsystem.image.uploadcover.repository.UploadCoverWriter;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.shared.time.IClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class UploadCoverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadCoverService.class);
    private final UploadCoverReader itemReader;
    private final UploadCoverWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final ImageStorage storage;
    private final IClock clock;

    public UploadCoverService(
            UploadCoverReader itemReader,
            UploadCoverWriter itemWriter,
            CurrentUserProvider currentUserProvider,
            ImageStorage storage,
            IClock clock) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.storage = storage;
        this.clock = clock;
    }

    public Mono<Result<UploadCoverResult>> uploadCover(UploadCoverCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        user ->
                                itemReader
                                        .findOwnedItem(command.itemId(), user.privateUserId())
                                        .flatMap(
                                                existing ->
                                                        command.image()
                                                                .switchIfEmpty(
                                                                        Mono.error(
                                                                                new ItemCoverValidationException(
                                                                                        "Item.InvalidCover")))
                                                                .flatMap(
                                                                        image ->
                                                                                save(
                                                                                        command
                                                                                                .itemId(),
                                                                                        user
                                                                                                .privateUserId(),
                                                                                        existing,
                                                                                        image)))
                                        .switchIfEmpty(
                                                Mono.fromSupplier(
                                                        () ->
                                                                failure(
                                                                        "Item.NotFound",
                                                                        "The item does not exist or cannot be updated."))))
                .onErrorResume(
                        ItemCoverValidationException.class,
                        error -> Mono.just(failure(error.errorCode(), "Cover validation failed.")));
    }

    private Mono<Result<UploadCoverResult>> save(
            UUID itemId, long ownerId, OwnedItemCover existing, ValidatedItemCover image) {
        return storage.upload(itemId, image.bytes(), image.contentType())
                .flatMap(
                        newKey ->
                                storage.previewUrl(newKey)
                                        .onErrorResume(
                                                error -> cleanup(newKey).then(Mono.error(error)))
                                        .flatMap(
                                                imageUrl ->
                                                        replace(
                                                                itemId,
                                                                ownerId,
                                                                existing.objectKey(),
                                                                newKey,
                                                                imageUrl)));
    }

    private Mono<Result<UploadCoverResult>> replace(
            UUID itemId, long ownerId, String oldKey, String newKey, String imageUrl) {
        return itemWriter
                .replace(itemId, ownerId, oldKey, newKey, clock.now())
                .doOnError(
                        error ->
                                LOGGER.error(
                                        "Cover update outcome unknown; reconcile object key {}",
                                        newKey,
                                        error))
                .flatMap(
                        updated -> {
                            if (!updated) {
                                return cleanup(newKey)
                                        .thenReturn(
                                                failure(
                                                        "Item.CoverUpdateConflict",
                                                        "The item changed during cover upload."));
                            }
                            return cleanup(oldKey)
                                    .thenReturn(
                                            Result.<UploadCoverResult>success(
                                                    new UploadCoverResult(itemId, imageUrl)));
                        });
    }

    private Mono<Void> cleanup(String key) {
        if (key == null) return Mono.empty();
        return storage.delete(key)
                .doOnError(
                        error -> LOGGER.warn("Cover cleanup failed for object key {}", key, error))
                .onErrorComplete();
    }

    private static Result<UploadCoverResult> failure(String code, String message) {
        return Result.failure(new ItemCoverDomainError(code, message));
    }
}
