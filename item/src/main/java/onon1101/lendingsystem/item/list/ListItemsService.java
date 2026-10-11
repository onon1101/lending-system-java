package onon1101.lendingsystem.item.list;

import java.util.List;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.infrastructure.image.ImagePreviewUrlResolver;
import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.web.pagination.PaginationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ListItemsService {

    private final ListItemsReader itemReader;
    private final CurrentUserProvider currentUserProvider;
    private final ImagePreviewUrlResolver imageResolver;

    public ListItemsService(
            ListItemsReader itemsReader,
            CurrentUserProvider currentUserProvider,
            ImagePreviewUrlResolver imageResolver) {

        this.itemReader = itemsReader;
        this.currentUserProvider = currentUserProvider;
        this.imageResolver = imageResolver;
    }

    @Transactional(readOnly = true)
    public Mono<ListItemResponse> list(PaginationRequest pagination) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(currentUser ->
                        itemReader
                                .findVisibleItems(currentUser.privateUserId(),
                                        pagination)
                                .collectList())
                .flatMap(items -> {

                    boolean hasNext = items.size() > pagination.size();
                    var paginationResponse = PaginationResponse.from(
                            pagination, hasNext);

                    return Flux
                            .fromIterable(items)
                            .take(pagination.size())
                            .concatMap(item -> imageResolver
                                    .resolve(item.coverObjectKey())
                                    .map(url -> ListItemResponse.ItemResponse.from(
                                            item, url))
                                    .defaultIfEmpty(
                                            ListItemResponse.ItemResponse.from(
                                                    item)))
                            .collectList()
                            .map(responses -> new ListItemResponse(
                                    responses,
                                    paginationResponse));
                });
    }
}
