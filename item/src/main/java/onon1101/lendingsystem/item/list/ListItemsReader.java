package onon1101.lendingsystem.item.list;

import reactor.core.publisher.Flux;

public interface ListItemsReader {

    Flux<ListedItem> findVisibleItems(long viewerId, ListItemsQuery query);
}
