package onon1101.lendingsystem.item.create;

import onon1101.lendingsystem.configurations.services.CommandResult;

import java.util.UUID;

public record CreateItemResult(
        UUID itemId
) implements CommandResult {
}
