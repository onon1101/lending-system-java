package onon1101.lendingsystem.infrastructure.image;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ImagePreviewUrlResolver {

    private final ObjectProvider<ImageStorage> imageStorageProvider;

    public ImagePreviewUrlResolver(ObjectProvider<ImageStorage> imageStorageProvider) {
        this.imageStorageProvider = imageStorageProvider;
    }

    public Mono<String> resolve(String objectKey) {
        return Mono.defer(() -> {

            if (objectKey == null || objectKey.isBlank()) {
                return Mono.empty();
            }

            ImageStorage storage = imageStorageProvider.getIfAvailable();

            if (storage == null) {
                return Mono.empty();
            }

            return storage.previewUrl(objectKey);
        });
    }
}
