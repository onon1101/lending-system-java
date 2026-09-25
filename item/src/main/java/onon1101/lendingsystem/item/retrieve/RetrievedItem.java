package onon1101.lendingsystem.item.retrieve;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;

public record RetrievedItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByViewer,
        Instant createdAt,
        Instant updatedAt) {

    public boolean canRequestBorrow() {
        return availability == ItemAvailability.AVAILABLE && !ownedByViewer;
    }
}
