package onon1101.lendingsystem.item.create;

import java.util.UUID;
import onon1101.lendingsystem.configurations.services.CommandResult;

public record CreateItemResult(UUID itemId) implements CommandResult {}
