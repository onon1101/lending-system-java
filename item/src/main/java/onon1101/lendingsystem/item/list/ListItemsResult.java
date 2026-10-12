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

}
