package onon1101.lendingsystem.item.publicList;

import onon1101.lendingsystem.infrastructure.image.ImagePreviewUrlResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StoragePublicItemPreviewUrlReader implements PublicItemPreviewUrlReader {

    private final ImagePreviewUrlResolver resolver;

    public StoragePublicItemPreviewUrlReader(ImagePreviewUrlResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Mono<String> resolve(String objectKey) {
        return resolver.resolve(objectKey);
    }
}
