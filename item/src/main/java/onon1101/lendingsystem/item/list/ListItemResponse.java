package onon1101.lendingsystem.item.list;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationResponse;

public record ListItemResponse(List<ItemResponse> items, PaginationResponse response) {

    public ListItemResponse {
        items = List.copyOf(items);
    }

    public static ListItemResponse from(ListItemsResult result) {
        var items = result.items().stream()
                .map(preview -> ItemResponse.from(preview.item(), preview.imageUrl()))
                .toList();
        return new ListItemResponse(items,
                new PaginationResponse(result.page(), result.size(), result.hasNext()));
    }

    public record ItemResponse(
            UUID itemId,
            UUID ownerId,
            String ownerUsername,
            String name,
            String description,
            ItemAvailability availability,
            boolean ownedByCurrentUser,
            boolean canRequestBorrow,
            Instant createdAt,
            Instant updatedAt,
            String imageUrl) {

        public static ItemResponse from(ListedItem item) {
            return from(item, null);
        }

        public static ItemResponse from(ListedItem item, String imageUrl) {
            return new ItemResponse(
                    item.itemId(),
                    item.ownerId(),
                    item.ownerUsername(),
                    item.name(),
                    item.description(),
                    item.availability(),
                    item.ownedByViewer(),
                    item.canRequestBorrow(),
                    item.createdAt(),
                    item.updatedAt(),
                    imageUrl);
        }
    }
}
