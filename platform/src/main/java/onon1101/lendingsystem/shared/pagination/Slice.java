package onon1101.lendingsystem.shared.pagination;
import java.util.List;
import java.util.Objects;
public record Slice<T>(List<T> items, boolean hasNext) {
    public Slice { items = List.copyOf(items); }
    public static <T> Slice<T> from(List<T> fetchedItems, PageSpec pagination) {
        Objects.requireNonNull(fetchedItems, "Fetched items must not be null.");
        Objects.requireNonNull(pagination, "Pagination must not be null.");
        int visibleCount = Math.min(fetchedItems.size(), pagination.size());
        return new Slice<>(fetchedItems.subList(0, visibleCount), fetchedItems.size() > pagination.size());
    }
}
