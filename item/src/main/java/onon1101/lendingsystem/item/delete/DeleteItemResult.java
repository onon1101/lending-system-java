package onon1101.lendingsystem.item.delete;

import onon1101.lendingsystem.configurations.services.CommandResult;

import java.time.Instant;
import java.util.UUID;

public record DeleteItemResult(
        UUID itemId,
        Instant archivedAt
) implements CommandResult {
}
