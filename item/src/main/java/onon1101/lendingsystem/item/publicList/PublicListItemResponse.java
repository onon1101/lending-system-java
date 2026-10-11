package onon1101.lendingsystem.item.publicList;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.web.pagination.PaginationResponse;

public record PublicListItemResponse(
        List<PublicItemResponse> items, PaginationResponse response) {
    public PublicListItemResponse {
        items = List.copyOf(items);
    }

    public record PublicItemResponse(
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
        public static PublicItemResponse fromPublicItem(PublicListedItem item) {
            return fromPublicItem(item, null);
        }

        public static PublicItemResponse fromPublicItem(PublicListedItem item, String imageUrl) {
            return new PublicItemResponse(
                    item.itemId(), item.ownerId(), item.ownerUsername(),
                    item.name(), item.description(), item.availability(),
                    false, false, item.createdAt(), item.updatedAt(), imageUrl);
        }
    }
}
