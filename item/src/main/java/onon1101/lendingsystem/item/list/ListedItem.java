package onon1101.lendingsystem.item.list;

import onon1101.lendingsystem.item.domain.ItemAvailability;

import java.time.Instant;
import java.util.UUID;

public record ListedItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByViewer,
        Instant createdAt,
        Instant updatedAt,
        String imageObjectKey
) {

    public boolean canRequestBorrow() {
        return availability == ItemAvailability.AVAILABLE && !ownedByViewer;
    }
}
