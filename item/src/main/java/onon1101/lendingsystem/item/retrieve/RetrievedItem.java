package onon1101.lendingsystem.item.retrieve;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.item.query.VisibleItem;

public record RetrievedItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByViewer,
        Instant createdAt,
        Instant updatedAt,
        String coverObjectKey) {

    public static RetrievedItem from(VisibleItem item) {
        return new RetrievedItem(
                item.itemId(),
                item.ownerId(),
                item.ownerUsername(),
                item.name(),
                item.description(),
                item.availability(),
                item.ownedByViewer(),
                item.createdAt(),
                item.updatedAt(),
                item.coverObjectKey());
    }

    public boolean canRequestBorrow() {
        return availability == ItemAvailability.AVAILABLE && !ownedByViewer;
    }
}
