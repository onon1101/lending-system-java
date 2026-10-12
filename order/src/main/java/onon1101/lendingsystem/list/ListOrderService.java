package onon1101.lendingsystem.list;

import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

@Service
public class ListOrderService {

    private final ListOrderReader orderReader;
    private final CurrentUserProvider currentUserProvider;

    public ListOrderService(
            ListOrderReader orderReader,
            CurrentUserProvider currentUserProvider
    ) {
        this.orderReader = orderReader;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public Mono<ListOrderResult> list(ListOrderQuery query) {

        return currentUserProvider
                .getCurrentUser()
                .flatMapMany(user -> orderReader
                        .findForUser(user.privateUserId(), query))
                .collectList()
                .map(order -> ListOrderResult.from(order, query));
    }
}
