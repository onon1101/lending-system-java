package onon1101.lendingsystem.item.uploadImage;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.shared.domain.Result;

import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;

import onon1101.lendingsystem.item.uploadImage.error.UploadImageDomainError;
import onon1101.lendingsystem.item.uploadImage.exception.UploadImageValidationException;
import onon1101.lendingsystem.item.uploadImage.repository.OwnedItemImage;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageWriter;
import onon1101.lendingsystem.item.uploadImage.repository.UploadImageReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled",
        havingValue = "true")
public class UploadImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            UploadImageService.class);
    private final UploadImageReader itemReader;
    private final UploadImageWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final ImageStorage storage;
    private final IClock clock;

    public UploadImageService(UploadImageReader itemReader,
                              UploadImageWriter itemWriter,
                              CurrentUserProvider currentUserProvider,
                              ImageStorage storage, IClock clock) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.storage = storage;
        this.clock = clock;
    }

    public Mono<Result<UploadImageResult>> upload(UploadImageCommand command) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(user -> {
                    var ownedItem = itemReader.findOwnedItem(command.itemId(),
                            user.privateUserId());

                    var invalidImage = Mono.<ValidatedImage>error(
                            new UploadImageValidationException(
                                    "Item.InvalidImage"));

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
                        UploadImageValidationException.class,
                        UploadImageService::ImageValidationFailure);
    }

    private static Mono<Result<UploadImageResult>> ImageValidationFailure(
            UploadImageValidationException exception
    ) {
        return Mono.just(
                failure(exception.errorCode(), "Image validation failed"));
    }

    private Mono<Result<UploadImageResult>> save(UUID itemId, long ownerId,
                                                 OwnedItemImage existing,
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

    private Mono<Result<UploadImageResult>> replace(UUID itemId, long ownerId,
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

    private Mono<Result<UploadImageResult>> updateResult(Boolean updated,
                                                         String newKey,
                                                         String oldKey,
                                                         UUID itemId,
                                                         String imageUrl) {

        if (!updated) {
            var failure = failure("Item.ImageUpdateConflict",
                    "The item changed during image upload.");

            return cleanup(newKey).thenReturn(failure);
        }

        var success = Result.success(new UploadImageResult(itemId, imageUrl));

        return cleanup(oldKey).thenReturn(success);
    }

    private static void updateFailure(String key, Throwable throwable) {
        LOGGER.error("Image update outcome unknown; reconcile object key {}",
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
        LOGGER.warn("Image cleanup failed for object key {}", key, throwable);
    }

    private static Result<UploadImageResult> failure(String code,
                                                     String message) {
        return Result.failure(new UploadImageDomainError(code, message));
    }
}
