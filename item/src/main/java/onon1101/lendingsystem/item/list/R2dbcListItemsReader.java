package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.item.query.R2dbcVisibleItemReader;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class R2dbcListItemsReader implements ListItemsReader {
    private final R2dbcVisibleItemReader itemReader;

    public R2dbcListItemsReader(R2dbcVisibleItemReader itemReader) {
        this.itemReader = itemReader;
    }

    @Override
    public Flux<ListedItem> findVisibleItems(long viewerId, ListItemsQuery query) {
        return itemReader.findVisibleItems(viewerId, query.pagination()).map(ListedItem::from);
    }
}
