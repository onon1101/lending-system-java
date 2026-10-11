package onon1101.lendingsystem.item.publicList;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;

public record PublicListedItem(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        Instant createdAt,
        Instant updatedAt,
        String coverObjectKey) {}
