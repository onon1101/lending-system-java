package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.configurations.context.user.CurrentUserProvider;

import onon1101.lendingsystem.configurations.pagination.PaginationRequest;

import onon1101.lendingsystem.configurations.pagination.PaginationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

@Service
public class ListItemsService {

    private final ListItemsReader itemReader;
    private final CurrentUserProvider currentUserProvider;

    public ListItemsService(
            ListItemsReader itemsReader,
            CurrentUserProvider currentUserProvider
    ) {
        this.itemReader = itemsReader;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public Mono<ListItemResponse> list(PaginationRequest pagination) {
        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        currentUser -> itemReader
                                .findVisibleItems(
                                        currentUser.privateUserId(),
                                        pagination)
                                .collectList()
                )
                .map(
                        items -> {
                    boolean hasNext = items.size() > pagination.size();

                    var response = items.stream()
                            .limit(pagination.size())
                            .map(ListItemResponse.ItemResponse::from)
                            .toList();

                    return new ListItemResponse(
                            response,
                            PaginationResponse.from(pagination, hasNext)
                    );
                });
    }
}
