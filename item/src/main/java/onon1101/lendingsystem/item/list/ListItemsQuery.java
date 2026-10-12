package onon1101.lendingsystem.item.list;
import java.util.Objects;
import onon1101.lendingsystem.shared.pagination.PageSpec;
public record ListItemsQuery(PageSpec pagination) {
    public ListItemsQuery {
        Objects.requireNonNull(pagination, "Pagination must not be null.");
    }
}
