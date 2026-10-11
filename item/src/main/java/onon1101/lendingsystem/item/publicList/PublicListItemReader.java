package onon1101.lendingsystem.item.publicList;

import onon1101.lendingsystem.web.pagination.PaginationRequest;
import reactor.core.publisher.Flux;

public interface PublicListItemReader {
    Flux<PublicListedItem> findAllPublicItems(PaginationRequest pagination);
}
