package onon1101.lendingsystem.item.publicList;

import java.util.List;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.web.pagination.PaginationResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PublicListItemService {
    private final PublicListItemReader publicListItemReader;
    private final ObjectProvider<ImageStorage> imageStorageProvider;

    public PublicListItemService(
            PublicListItemReader publicListItemReader,
            ObjectProvider<ImageStorage> imageStorageProvider) {
        this.publicListItemReader = publicListItemReader;
        this.imageStorageProvider = imageStorageProvider;
    }

    @Transactional(readOnly = true)
    public Mono<PublicListItemResponse> listPublic(PaginationRequest pagination) {
        return publicListItemReader.findAllPublicItems(pagination)
                .collectList()
                .flatMap(items -> toPublicResponse(items, pagination));
    }

    private Mono<PublicListItemResponse> toPublicResponse(
            List<PublicListedItem> items, PaginationRequest pagination) {
        boolean hasNext = items.size() > pagination.size();
        var paginationResponse = PaginationResponse.from(pagination, hasNext);
        return Flux.fromIterable(items)
                .take(pagination.size())
                .concatMap(this::toPublicPreviewResponse)
                .collectList()
                .map(responses -> new PublicListItemResponse(responses, paginationResponse));
    }

    private Mono<PublicListItemResponse.PublicItemResponse> toPublicPreviewResponse(
            PublicListedItem item) {
        ImageStorage storage = imageStorageProvider.getIfAvailable();
        String key = item.coverObjectKey();
        if (storage == null || key == null || key.isBlank()) {
            return Mono.just(PublicListItemResponse.PublicItemResponse.fromPublicItem(item));
        }
        return storage.previewUrl(key)
                .map(url -> PublicListItemResponse.PublicItemResponse.fromPublicItem(item, url));
    }
}
