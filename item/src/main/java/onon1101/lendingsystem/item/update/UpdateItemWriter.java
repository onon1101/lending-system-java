package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.item.domain.Item;
import reactor.core.publisher.Mono;

public interface UpdateItemWriter {

    Mono<Boolean> update(Item item);
}
