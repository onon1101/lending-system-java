package onon1101.lendingsystem.item.retrieve;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface RetrieveItemReader {

    Mono<RetrievedItem> findVisibleItem(UUID itemId, long viewId);
}
