package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;

import onon1101.lendingsystem.web.pagination.PaginationRequest;

import onon1101.lendingsystem.web.pagination.PaginationResponse;
import onon1101.lendingsystem.infrastructure.image.ImageStorage;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class ListItemsService {

    private final ListItemsReader itemReader;
    private final CurrentUserProvider currentUserProvider;
    private final ObjectProvider<ImageStorage> imageStorageProvider;

    public ListItemsService(
            ListItemsReader itemsReader,
            CurrentUserProvider currentUserProvider,
            ObjectProvider<ImageStorage> imageStorageProvider
    ) {
        this.itemReader = itemsReader;
        this.currentUserProvider = currentUserProvider;
        this.imageStorageProvider = imageStorageProvider;
    }

    @Transactional(readOnly = true)
    public Mono<ListItemResponse> list(PaginationRequest pagination) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser -> itemReader
                                .findVisibleItems(
                                        currentUser.privateUserId(),
                                        pagination)
                                .collectList()
                )
                .flatMap(
                        items -> {
                    boolean hasNext = items.size() > pagination.size();

                    return Flux.fromIterable(items)
                            .take(pagination.size())
                            .concatMap(this::toResponse)
                            .collectList()
                            .map(response -> new ListItemResponse(
                                    response,
                                    PaginationResponse.from(pagination, hasNext)));
                });
    }

    private Mono<ListItemResponse.ItemResponse> toResponse(ListedItem item) {
        ImageStorage storage = imageStorageProvider.getIfAvailable();
        String key = item.imageObjectKey();

        if (storage == null || key == null || key.isBlank()) {
            return Mono.just(ListItemResponse.ItemResponse.from(item));
        }

        return storage.previewUrl(key)
                .map(url -> ListItemResponse.ItemResponse.from(item, url));
    }
}
