package onon1101.lendingsystem.item.retrieve;


import onon1101.lendingsystem.configurations.services.CommandResult;
import onon1101.lendingsystem.item.domain.ItemAvailability;

import java.time.Instant;
import java.util.UUID;

public record RetrieveItemResult(
        UUID itemId,
        UUID ownerId,
        String ownerUsername,
        String name,
        String description,
        ItemAvailability availability,
        boolean ownedByCurrentUser,
        boolean canRequestBorrow,
        Instant createdAt,
        Instant updatedAt)
        implements CommandResult {

    public static RetrieveItemResult from(RetrievedItem item) {
        return new RetrieveItemResult(
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
