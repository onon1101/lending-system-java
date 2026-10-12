package onon1101.lendingsystem.list;

import java.util.List;

public record ListOrderResult(
        List<ListedOrder> orders,
        int page,
        int size,
        boolean hasNext
) {

    public ListOrderResult {
        orders = List.copyOf(orders);
    }

    public static ListOrderResult from(
            List<ListedOrder> fetchOrders,
            ListOrderQuery query
    ) {
        int visibleCount = Math.min(
                fetchOrders.size(),
                query.size()
        );

        return new ListOrderResult(
                fetchOrders.subList(0, visibleCount),
                query.page(),
                query.size(),
                fetchOrders.size() > query.size()
        );
    }
}
