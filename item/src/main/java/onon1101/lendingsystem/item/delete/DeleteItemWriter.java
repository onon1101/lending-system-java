package onon1101.lendingsystem.item.delete;

import java.time.Instant;
import java.util.UUID;
import reactor.core.publisher.Mono;

public interface DeleteItemWriter {

    Mono<Boolean> archiveOwnedItem(UUID itemId, long ownerId, Instant archivedAt);
}
