package onon1101.lendingsystem.item.retrieve;


import io.swagger.v3.oas.annotations.media.Schema;
import onon1101.lendingsystem.item.domain.ItemAvailability;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "物品詳細資訊")
public record RetrieveItemResponse(
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

    public static RetrieveItemResponse from(
            RetrieveItemResult result) {
        return new RetrieveItemResponse(
                result.itemId(),
                result.ownerId(),
                result.ownerUsername(),
                result.name(),
                result.description(),
                result.availability(),
                result.ownedByCurrentUser(),
                result.canRequestBorrow(),
                result.createdAt(),
                result.updatedAt());
    }
}
