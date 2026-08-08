package onon1101.lendingsystem.item.retrieve;

import onon1101.lendingsystem.item.domain.ItemAvailability;

import java.time.Instant;
import java.util.UUID;

public record RetrievedItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByViewer,
        Instant createdAt,
        Instant updatedAt
) {

    public boolean canRequestBorrow() {
        return availability == ItemAvailability.AVAILABLE
                && !ownedByViewer;
    }
}
