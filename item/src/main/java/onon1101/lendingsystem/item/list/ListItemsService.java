package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.application.currentuser.CurrentUserContext;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.web.pagination.PaginationResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ListItemsService {

    private final ListItemsReader itemReader;
    private final CurrentUserProvider currentUserProvider;
    private final ObjectProvider<ImageStorage> imageStorageProvider;

    public ListItemsService(
            ListItemsReader itemsReader,
            CurrentUserProvider currentUserProvider,
            ObjectProvider<ImageStorage> imageStorageProvider) {
        this.itemReader = itemsReader;
        this.currentUserProvider = currentUserProvider;
        this.imageStorageProvider = imageStorageProvider;
    }
    @Transactional(readOnly = true)
    public Mono<ListItemResponse> list(PaginationRequest pagination) {
        return currentUserProvider.getCurrentUser()
                .flatMap(currentUser -> findVisibleItems(currentUser, pagination))
                .flatMap(items -> toResponse(items, pagination));
    }

    private Mono<ListItemResponse> toResponse(
            List<ListedItem> items,
            PaginationRequest pagination) {

        boolean hasNext = items.size() > pagination.size();
        var paginationResponse = PaginationResponse.from(pagination, hasNext);

        return Flux.fromIterable(items)
                .take(pagination.size())
                .concatMap(this::toPreviewUrl)
                .collectList()
                .map(responses -> new ListItemResponse(
                        responses,
                        paginationResponse));
    }

    private Mono<List<ListedItem>> findVisibleItems(
            CurrentUserContext currentUser,
            PaginationRequest pagination) {

        return itemReader.findVisibleItems(currentUser.privateUserId(), pagination)
                .collectList();
    }

    private Mono<ListItemResponse.ItemResponse> toPreviewUrl(ListedItem item) {
        ImageStorage storage = imageStorageProvider.getIfAvailable();
        String key = item.coverObjectKey();

        if (storage == null || key == null || key.isBlank()) {
            return Mono.just(ListItemResponse.ItemResponse.from(item));
        }

        return storage.previewUrl(key)
                .map(url -> ListItemResponse.ItemResponse.from(item, url));
    }
}
