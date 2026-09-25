package onon1101.lendingsystem.item.update;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.configurations.services.CommandResult;

public record UpdateItemResult(UUID itemId, String name, String description, Instant updatedAt)
        implements CommandResult {}
