package onon1101.lendingsystem.item.delete;

import java.util.UUID;
import onon1101.lendingsystem.configurations.services.Command;

public record DeleteItemCommand(UUID itemId) implements Command {}
