package onon1101.lendingsystem.item.retrieve;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.item.domain.ItemAvailability;
import onon1101.lendingsystem.shared.command.CommandResult;

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
        Instant updatedAt,
        String imageUrl)
        implements CommandResult {

    public static RetrieveItemResult from(RetrievedItem item) {
        return from(item, null);
    }

    public static RetrieveItemResult from(RetrievedItem item, String imageUrl) {
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
                item.updatedAt(),
                imageUrl);
    }
}
