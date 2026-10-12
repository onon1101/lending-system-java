package onon1101.lendingsystem.list;
import java.util.List;
import onon1101.lendingsystem.shared.pagination.Slice;
public record ListOrderResult(List<ListedOrder> orders, int page, int size, boolean hasNext) {
    public ListOrderResult { orders = List.copyOf(orders); }
    public static ListOrderResult from(List<ListedOrder> fetchedOrders, ListOrderQuery query) {
        var pagination = query.pagination();
        var slice = Slice.from(fetchedOrders, pagination);
        return new ListOrderResult(slice.items(), pagination.page(), pagination.size(), slice.hasNext());
    }
}
