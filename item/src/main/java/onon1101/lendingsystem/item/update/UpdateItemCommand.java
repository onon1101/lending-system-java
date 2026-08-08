package onon1101.lendingsystem.item.update;

import onon1101.lendingsystem.configurations.services.Command;

import java.util.UUID;

public record UpdateItemCommand(
        UUID itemId,
        String name,
        String description
) implements Command  {
}
