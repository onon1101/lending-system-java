package onon1101.lendingsystem.item.list;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.item.query.VisibleItem;

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
        String coverObjectKey) {

    public static ListedItem from(VisibleItem item) {
        return new ListedItem(
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
