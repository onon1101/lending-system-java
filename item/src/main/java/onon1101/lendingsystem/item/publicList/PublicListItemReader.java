package onon1101.lendingsystem.item.publicList;

import reactor.core.publisher.Flux;

public interface PublicListItemReader {
    Flux<PublicListedItem> findAllPublicItems(PublicListItemsQuery query);
}
