package onon1101.lendingsystem.item.delete;

import java.time.Instant;
import java.util.UUID;
import onon1101.lendingsystem.shared.command.CommandResult;

public record DeleteItemResult(UUID itemId, Instant archivedAt) implements CommandResult {}
