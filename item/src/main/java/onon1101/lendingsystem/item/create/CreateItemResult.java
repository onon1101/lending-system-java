package onon1101.lendingsystem.item.create;

import java.util.UUID;
import onon1101.lendingsystem.application.idempotency.IdempotencyResult;
import onon1101.lendingsystem.shared.command.CommandResult;

public record CreateItemResult(UUID itemId) implements CommandResult, IdempotencyResult {}
