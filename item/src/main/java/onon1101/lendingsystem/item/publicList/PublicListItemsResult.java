package onon1101.lendingsystem.item.publicList;

import java.util.List;

public record PublicListItemsResult(List<ItemPreview> items, int page, int size, boolean hasNext) {
    public PublicListItemsResult {
        items = List.copyOf(items);
    }

    public record ItemPreview(PublicListedItem item, String imageUrl) {}

}
