package onon1101.lendingsystem.infrastructure.image;

import java.time.Duration;
import java.util.UUID;

import onon1101.lendingsystem.infrastructure.storage.rustfs.RustfsProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled",
        havingValue = "true")
public class ImageStorage {

    private final S3Client client;
    private final S3Presigner presigner;
    private final RustfsProperties properties;

    public ImageStorage(S3Client client, S3Presigner presigner,
                        RustfsProperties properties) {
        this.client = client;
        this.presigner = presigner;
        this.properties = properties;
    }

    // The caller must verify ownership, file size, and decoded image content
    // first.
    public Mono<String> upload(UUID itemId, byte[] bytes, String contentType) {
        return Mono
                .fromCallable(() -> uploadImage(itemId, bytes, contentType))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String uploadImage(UUID itemId, byte[] bytes, String contentType) {
        String extension = contentType2ImageExtensionConvertor(contentType);

        String key = concatImagePath(itemId, extension);

        client.putObject(
                PutObjectRequest
                        .builder()
                        .bucket(properties.bucket())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(bytes));

        return key;
    }

    private static String contentType2ImageExtensionConvertor(
            String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException(
                    "Unsupported image type.");
        };
    }

    private static String concatImagePath(UUID itemId, String extension) {
        return "items/" + itemId + "/" + UUID.randomUUID() + "." + extension;
    }

    public Mono<String> previewUrl(String key) {
        return Mono
                .fromCallable(() -> getPreviewUrl(key))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String getPreviewUrl(String key) {
        var objectRequest = GetObjectRequest
                .builder()
                .bucket(properties.bucket())
                .key(key)
                .build();

        var presignRequest =
                GetObjectPresignRequest
                        .builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(objectRequest)
                        .build();

        return presigner
                .presignGetObject(presignRequest)
                .url()
                .toString();
    }

    public Mono<Void> delete(String key) {
        return Mono
                .fromRunnable(() -> deleteImage(key))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void deleteImage(String key) {
        var objectRequest =
                DeleteObjectRequest
                        .builder()
                        .bucket(properties.bucket())
                        .key(key)
                        .build();

        client.deleteObject(objectRequest);
    }
}
