package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.infrastructure.image.ImagePreviewUrlResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StorageItemPreviewUrlReader implements ItemPreviewUrlReader {
    private final ImagePreviewUrlResolver resolver;

    public StorageItemPreviewUrlReader(ImagePreviewUrlResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Mono<String> resolve(String objectKey) {
        return resolver.resolve(objectKey);
    }
}
