package onon1101.lendingsystem.item.update;

import java.util.UUID;
import onon1101.lendingsystem.configurations.services.Command;

public record UpdateItemCommand(UUID itemId, String name, String description) implements Command {}
