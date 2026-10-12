package onon1101.lendingsystem.item.list;
import onon1101.lendingsystem.shared.pagination.Slice;


import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ListItemsService {

    private final ListItemsReader itemReader;
    private final CurrentUserProvider currentUserProvider;
    private final ItemPreviewUrlReader previewUrlReader;

    public ListItemsService(
            ListItemsReader itemReader,
            CurrentUserProvider currentUserProvider,
            ItemPreviewUrlReader previewUrlReader) {

        this.itemReader = itemReader;
        this.currentUserProvider = currentUserProvider;
        this.previewUrlReader = previewUrlReader;
    }

    public Mono<ListItemsResult> list(ListItemsQuery query) {
        return currentUserProvider
                .getCurrentUser()
                .flatMapMany(user -> itemReader.findVisibleItems(user.privateUserId(), query))
                .collectList()
                .map(items -> Slice.from(items, query.pagination()))
                .flatMap(slice -> resolvePreviews(slice, query));
    }

    private Mono<ListItemsResult> resolvePreviews(Slice<ListedItem> slice, ListItemsQuery query) {
        return Flux
                .fromIterable(slice.items())
                .concatMap(this::resolvePreview)
                .collectList()
                .map(items -> new ListItemsResult(items, query.pagination().page(), query.pagination().size(), slice.hasNext()));
    }

    private Mono<ListItemsResult.ItemPreview> resolvePreview(ListedItem item) {
        return previewUrlReader.resolve(item.coverObjectKey())
                .map(previewUrl -> new ListItemsResult.ItemPreview(item, previewUrl))
                .defaultIfEmpty(new ListItemsResult.ItemPreview(item, null));
    }
}
