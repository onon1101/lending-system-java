package onon1101.lendingsystem.image.uploadimage;

import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.shared.time.IClock;
import onon1101.lendingsystem.image.uploadimage.error.ItemImageDomainError;
import onon1101.lendingsystem.image.uploadimage.error.ItemImageValidationException;
import onon1101.lendingsystem.image.uploadimage.repository.UploadItemImageReader;
import onon1101.lendingsystem.image.uploadimage.repository.UploadItemImageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class UploadItemImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadItemImageService.class);
    private final UploadItemImageReader itemReader;
    private final UploadItemImageWriter itemWriter;
    private final CurrentUserProvider currentUserProvider;
    private final ImageStorage storage;
    private final IClock clock;

    public UploadItemImageService(
            UploadItemImageReader itemReader,
            UploadItemImageWriter itemWriter,
            CurrentUserProvider currentUserProvider,
            ImageStorage storage,
            IClock clock) {
        this.itemReader = itemReader;
        this.itemWriter = itemWriter;
        this.currentUserProvider = currentUserProvider;
        this.storage = storage;
        this.clock = clock;
    }

    public Mono<Result<UploadItemImageResult>> upload(UploadItemImageCommand command) {
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
                                                                                new ItemImageValidationException(
                                                                                        "Item.InvalidImage")))
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
                        ItemImageValidationException.class,
                        error -> Mono.just(failure(error.errorCode(), "Image validation failed.")));
    }

    private Mono<Result<UploadItemImageResult>> save(
            UUID itemId, long ownerId, OwnedItemImage existing, ValidatedItemImage image) {
        return storage.uploadValidatedImage(itemId, image.bytes(), image.contentType())
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

    private Mono<Result<UploadItemImageResult>> replace(
            UUID itemId, long ownerId, String oldKey, String newKey, String imageUrl) {
        return itemWriter
                .replace(itemId, ownerId, oldKey, newKey, clock.now())
                .doOnError(
                        error ->
                                LOGGER.error(
                                        "Image update outcome unknown; reconcile object key {}",
                                        newKey,
                                        error))
                .flatMap(
                        updated -> {
                            if (!updated) {
                                return cleanup(newKey)
                                        .thenReturn(
                                                failure(
                                                        "Item.ImageUpdateConflict",
                                                        "The item changed during image upload."));
                            }
                            return cleanup(oldKey)
                                    .thenReturn(
                                            Result.<UploadItemImageResult>success(
                                                    new UploadItemImageResult(itemId, imageUrl)));
                        });
    }

    private Mono<Void> cleanup(String key) {
        if (key == null) return Mono.empty();
        return storage.delete(key)
                .doOnError(
                        error -> LOGGER.warn("Image cleanup failed for object key {}", key, error))
                .onErrorComplete();
    }

    private static Result<UploadItemImageResult> failure(String code, String message) {
        return Result.failure(new ItemImageDomainError(code, message));
    }
}
