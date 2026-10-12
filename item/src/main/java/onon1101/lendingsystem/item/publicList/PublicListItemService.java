package onon1101.lendingsystem.item.publicList;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PublicListItemService {
    private final PublicListItemReader itemReader;
    private final PublicItemPreviewUrlReader previewUrlReader;

    public PublicListItemService(
            PublicListItemReader itemReader, PublicItemPreviewUrlReader previewUrlReader) {
        this.itemReader = itemReader;
        this.previewUrlReader = previewUrlReader;
    }

    public Mono<PublicListItemsResult> listPublic(PublicListItemsQuery query) {
        return itemReader
                .findAllPublicItems(query)
                .collectList()
                .map(items -> PublicListItemsResult.Slice.from(items, query))
                .flatMap(slice -> resolvePreviews(slice, query));
    }

    private Mono<PublicListItemsResult> resolvePreviews(
            PublicListItemsResult.Slice slice, PublicListItemsQuery query) {
        return Flux.fromIterable(slice.items())
                .concatMap(this::resolvePreview)
                .collectList()
                .map(
                        items ->
                                new PublicListItemsResult(
                                        items, query.page(), query.size(), slice.hasNext()));
    }

    private Mono<PublicListItemsResult.ItemPreview> resolvePreview(PublicListedItem item) {
        return previewUrlReader
                .resolve(item.coverObjectKey())
                .map(url -> new PublicListItemsResult.ItemPreview(item, url))
                .defaultIfEmpty(new PublicListItemsResult.ItemPreview(item, null));
    }
}
