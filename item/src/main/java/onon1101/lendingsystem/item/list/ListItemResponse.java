package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.configurations.pagination.PaginationResponse;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.item.retrieve.RetrieveItemResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// todo: 把 page response 抽象成一個類
// todo: 去除 RetrieveItemResponse，而使用自己的資訊
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
        Instant updatedAt) {

        public static ItemResponse from(ListedItem item) {
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
                    item.updatedAt());
        }
    }
}
