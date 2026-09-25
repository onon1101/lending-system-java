package onon1101.lendingsystem.item.update;

import java.util.UUID;
import onon1101.lendingsystem.item.domain.Item;
import reactor.core.publisher.Mono;

public interface UpdateItemReader {

    Mono<Item> finOwnedItem(UUID itemId, long ownerId);
}
