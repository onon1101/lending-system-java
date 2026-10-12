package onon1101.lendingsystem.item.list;

import java.util.List;

public record ListItemsResult(
        List<ItemPreview> items,
        int page,
        int size,
        boolean hasNext
) {

    public ListItemsResult {
        items = List.copyOf(items);
    }

    public record ItemPreview(ListedItem item, String imageUrl) {}

    public record Slice(List<ListedItem> items, boolean hasNext) {
        public Slice {
            items = List.copyOf(items);
        }

        public static Slice from(
                List<ListedItem> fetchedItems,
                ListItemsQuery query
        ) {
            int visibleCount =  Math.min(fetchedItems.size(), query.size());

            return new Slice(
                    fetchedItems.subList(0, visibleCount),
                    fetchedItems.size() > query.size()
            );
        }
    }
}
