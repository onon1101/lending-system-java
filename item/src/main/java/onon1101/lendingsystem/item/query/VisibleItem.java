package onon1101.lendingsystem.item.query;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;

public record VisibleItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByViewer,
        Instant createdAt,
        Instant updatedAt,
        String coverObjectKey) {}
