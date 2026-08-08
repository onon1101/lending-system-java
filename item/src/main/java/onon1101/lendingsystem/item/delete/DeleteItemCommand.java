package onon1101.lendingsystem.item.delete;

import onon1101.lendingsystem.configurations.services.Command;

import java.util.UUID;

public record DeleteItemCommand(
        UUID itemId
) implements Command {
}
