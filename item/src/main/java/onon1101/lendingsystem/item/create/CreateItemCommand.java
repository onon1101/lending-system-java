package onon1101.lendingsystem.item.create;


import onon1101.lendingsystem.configurations.services.Command;

public record CreateItemCommand(
        String name,
        String description
) implements Command{
}
