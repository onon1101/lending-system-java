package onon1101.lendingsystem.item.uploadCover;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.shared.domain.Result;

import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;

import onon1101.lendingsystem.item.uploadCover.error.UploadCoverDomainError;
import onon1101.lendingsystem.item.uploadCover.exception.UploadCoverValidationException;
import onon1101.lendingsystem.item.uploadCover.repository.OwnedItemCover;
import onon1101.lendingsystem.item.uploadCover.repository.UploadCoverWriter;
import onon1101.lendingsystem.item.uploadCover.repository.UploadCoverReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled",
        havingValue = "true")
public class UploadCoverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            UploadCoverService.class);
    private final UploadCoverReader itemReader;
    private final UploadCoverWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final ImageStorage storage;
    private final IClock clock;

    public UploadCoverService(UploadCoverReader itemReader,
                              UploadCoverWriter itemWriter,
                              CurrentUserProvider currentUserProvider,
                              ImageStorage storage, IClock clock) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.storage = storage;
        this.clock = clock;
    }

    public Mono<Result<UploadCoverResult>> uploadCover(UploadCoverCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> {
                    var ownedItem = itemReader.findOwnedItem(command.itemId(),
                            user.privateUserId());

                    var invalidImage = Mono.<ValidatedImage>error(
                            new UploadCoverValidationException(
                                    "Item.InvalidCover"));

                    var itemNotFound = Mono.fromSupplier(
                            () -> failure("Item.NotFound",
                                    "The item does not exist or " +
                                            "cannot be updated."));

                    return ownedItem
                            .flatMap(existing -> command
                                    .image()
                                    .switchIfEmpty(invalidImage)
                                    .flatMap(image -> save(
                                            command.itemId(),
                                            user.privateUserId(),
                                            existing,
                                            image)))
                            .switchIfEmpty(itemNotFound);
                })
                .onErrorResume(
                        UploadCoverValidationException.class,
                        UploadCoverService::coverValidationFailure);
    }

    private static Mono<Result<UploadCoverResult>> coverValidationFailure(
            UploadCoverValidationException exception
    ) {
        return Mono.just(
                failure(exception.errorCode(), "Cover validation failed"));
    }

    private Mono<Result<UploadCoverResult>> save(UUID itemId, long ownerId,
                                                 OwnedItemCover existing,
                                                 ValidatedImage image) {

        return storage
                .upload(itemId, image.bytes(),
                        image.contentType())
                .flatMap(newKey -> storage
                        .previewUrl(newKey)
                        .onErrorResume(error -> cleanup(newKey).then(
                                Mono.error(error)))
                        .flatMap(imageUrl -> replace(itemId, ownerId,
                                existing.objectKey(), newKey, imageUrl)));
    }

    private Mono<Result<UploadCoverResult>> replace(UUID itemId, long ownerId,
                                                    String oldKey,
                                                    String newKey,
                                                    String imageUrl) {
        return itemWriter
                .replace(itemId, ownerId, oldKey, newKey, clock.now())
                .doOnError(error -> updateFailure(newKey, error))
                .flatMap(
                        updated -> updateResult(updated, newKey, oldKey, itemId,
                                imageUrl));
    }

    private Mono<Result<UploadCoverResult>> updateResult(Boolean updated,
                                                         String newKey,
                                                         String oldKey,
                                                         UUID itemId,
                                                         String imageUrl) {

        if (!updated) {
            var failure = failure("Item.CoverUpdateConflict",
                    "The item changed during cover upload.");

            return cleanup(newKey).thenReturn(failure);
        }

        var success = Result.success(new UploadCoverResult(itemId, imageUrl));

        return cleanup(oldKey).thenReturn(success);
    }

    private static void updateFailure(String key, Throwable throwable) {
        LOGGER.error("Cover update outcome unknown; reconcile object key {}",
                key, throwable);
    }

    private Mono<Void> cleanup(String key) {
        if (key == null) {
            return Mono.empty();
        }

        return storage
                .delete(key)
                .doOnError(error -> cleanupWarning(key, error))
                .onErrorComplete();
    }

    private static void cleanupWarning(String key, Throwable throwable) {
        LOGGER.warn("Cover cleanup failed for object key {}", key, throwable);
    }

    private static Result<UploadCoverResult> failure(String code,
                                                     String message) {
        return Result.failure(new UploadCoverDomainError(code, message));
    }
}
