package onon1101.lendingsystem.item.create;

import onon1101.lendingsystem.item.domain.Item;
import reactor.core.publisher.Mono;

public interface CreateItemWriter {

    Mono<Void> create(Item item);
}
