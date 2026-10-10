package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.web.pagination.PaginationResponse;
import onon1101.lendingsystem.item.domain.ItemAvailability;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// todo: 把 page response 抽象成一個類
public record ListItemResponse(
        List<ItemResponse> items,
        PaginationResponse response
) {

    public ListItemResponse {
        items = List.copyOf(items);
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
