package onon1101.lendingsystem.list;

import reactor.core.publisher.Flux;

public interface ListOrderReader {

    Flux<ListedOrder> findForUser(
            long userId,
            ListOrderQuery query
    );
}
