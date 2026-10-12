package onon1101.lendingsystem.item.publicList;

import java.util.List;

public record PublicListItemsResult(List<ItemPreview> items, int page, int size, boolean hasNext) {
    public PublicListItemsResult {
        items = List.copyOf(items);
    }

    public record ItemPreview(PublicListedItem item, String imageUrl) {}

    public record Slice(List<PublicListedItem> items, boolean hasNext) {

        public Slice {
            items = List.copyOf(items);
        }

        public static Slice from(List<PublicListedItem> fetchedItems, PublicListItemsQuery query) {
            int visibleCount = Math.min(fetchedItems.size(), query.size());

            boolean hasNext = fetchedItems.size() > query.size();

            return new Slice(fetchedItems.subList(0, visibleCount), hasNext);
        }
    }
}
