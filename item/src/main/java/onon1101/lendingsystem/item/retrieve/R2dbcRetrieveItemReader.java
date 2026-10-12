package onon1101.lendingsystem.item.retrieve;

import java.util.UUID;
import onon1101.lendingsystem.item.query.R2dbcVisibleItemReader;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcRetrieveItemReader implements RetrieveItemReader {
    private final R2dbcVisibleItemReader itemReader;

    public R2dbcRetrieveItemReader(R2dbcVisibleItemReader itemReader) {
        this.itemReader = itemReader;
    }

    @Override
    public Mono<RetrievedItem> findVisibleItem(UUID itemId, long viewerId) {
        return itemReader.findVisibleItem(itemId, viewerId).map(RetrievedItem::from);
    }
}
