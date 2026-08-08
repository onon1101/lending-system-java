package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.configurations.services.CommandResult;

import java.time.Instant;
import java.util.UUID;

public record UpdateItemResult(
        UUID itemId,
        String name,
        String description,
        Instant updatedAt
) implements CommandResult {
}
