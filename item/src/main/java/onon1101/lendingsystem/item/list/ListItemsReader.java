package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.web.pagination.PaginationRequest;
import onon1101.lendingsystem.item.retrieve.RetrievedItem;
import reactor.core.publisher.Flux;

public interface ListItemsReader {

    Flux<ListedItem> findVisibleItems(
            long viewerId,
            PaginationRequest pagination
    );
}
