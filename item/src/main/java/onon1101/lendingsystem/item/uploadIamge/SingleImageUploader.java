package onon1101.lendingsystem.item.uploadIamge;

import java.util.UUID;
import onon1101.lendingsystem.infrastructure.image.ImageReader;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.infrastructure.image.ImageValidationException;
import onon1101.lendingsystem.infrastructure.image.ValidatedImage;
import onon1101.lendingsystem.item.uploadIamge.repository.SavedImage;
import onon1101.lendingsystem.item.uploadIamge.repository.UploadImageWriter;
import onon1101.lendingsystem.shared.time.IClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class SingleImageUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleImageUploader.class);

    private final ImageReader imageReader;
    private final ImageStorage storage;
    private final UploadImageWriter itemWriter;
    private final IClock clock;

    public SingleImageUploader(
            ImageReader imageReader,
            ImageStorage storage,
            UploadImageWriter itemWriter,
            IClock clock) {
        this.imageReader = imageReader;
        this.storage = storage;
        this.itemWriter = itemWriter;
        this.clock = clock;
    }

    public Mono<ImageOutcome> upload(UUID itemId, long ownerId, int index, Part part) {

        if (!(part instanceof FilePart file)) {
            return Mono.just(rejected(index, "Item.InvalidImage"));
        }

        return imageReader
                .read(file)
                .flatMap(
                        image -> {
                            return uploadAndSave(itemId, ownerId, index, image);
                        })
                .onErrorResume(
                        ImageValidationException.class,
                        error -> {
                            String errorCode = validationErrorCode(error);

                            return Mono.just(rejected(index, errorCode));
                        })
                .onErrorResume(
                        error -> {
                            LOGGER.error(
                                    "Image upload failed for item {}, index {}",
                                    itemId,
                                    index,
                                    error);

                            return Mono.just(rejected(index, "Item.ImageUploadFailed"));
                        });
    }

    private Mono<ImageOutcome> uploadAndSave(
            UUID itemId, long ownerId, int index, ValidatedImage image) {

        return storage.upload(itemId, image.bytes(), image.contentType())
                .flatMap(
                        objectKey -> {
                            return previewUrlOrCleanup(objectKey)
                                    .flatMap(
                                            imageUrl -> {
                                                return saveImage(
                                                        itemId, ownerId, index, image, objectKey,
                                                        imageUrl);
                                            });
                        });
    }

    private Mono<String> previewUrlOrCleanup(String objectKey) {
        return storage.previewUrl(objectKey)
                .onErrorResume(
                        error -> {
                            // 尚未寫入資料庫，可以安全刪除新物件。
                            return cleanup(objectKey).then(Mono.<String>error(error));
                        });
    }

    private Mono<ImageOutcome> saveImage(
            UUID itemId,
            long ownerId,
            int index,
            ValidatedImage image,
            String objectKey,
            String imageUrl) {

        return itemWriter
                .append(itemId, ownerId, objectKey, image.contentType(), clock.now())
                .map(
                        saved -> {
                            return accepted(index, saved, imageUrl);
                        })
                .switchIfEmpty(
                        Mono.defer(
                                () -> {
                                    // Repository 確認未新增紀錄，清除新物件。
                                    var outcome = rejected(index, "Item.NotFound");

                                    return cleanup(objectKey).thenReturn(outcome);
                                }))
                .onErrorResume(
                        error -> {
                            // 交易可能已提交，保留物件以免刪除已被引用的圖片。
                            LOGGER.error(
                                    "Image save outcome unknown for item {}, "
                                            + "index {}; reconcile object key {}",
                                    itemId,
                                    index,
                                    objectKey,
                                    error);

                            return Mono.just(rejected(index, "Item.ImageSaveUncertain"));
                        });
    }

    private Mono<Void> cleanup(String objectKey) {
        return storage.delete(objectKey)
                .doOnError(
                        error -> {
                            LOGGER.warn("Image cleanup failed for object key {}", objectKey, error);
                        })
                .onErrorComplete();
    }

    private static String validationErrorCode(ImageValidationException error) {

        return switch (error.errorCode()) {
            case "Item.CoverTooLarge" -> "Item.ImageTooLarge";
            default -> "Item.InvalidImage";
        };
    }

    private static ImageOutcome accepted(int index, SavedImage saved, String imageUrl) {

        return new ImageOutcome(index, saved.imageId(), saved.sortOrder(), imageUrl, null);
    }

    private static ImageOutcome rejected(int index, String errorCode) {
        return new ImageOutcome(index, null, null, null, errorCode);
    }
}
