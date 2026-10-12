package onon1101.lendingsystem.item.publicList;
import java.util.Objects;
import onon1101.lendingsystem.shared.pagination.PageSpec;
public record PublicListItemsQuery(PageSpec pagination) {
    public PublicListItemsQuery {
        Objects.requireNonNull(pagination, "Pagination must not be null.");
    }
}
