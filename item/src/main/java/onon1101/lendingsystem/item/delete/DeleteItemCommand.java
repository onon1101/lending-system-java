package onon1101.lendingsystem.item.delete;

import java.util.UUID;
import onon1101.lendingsystem.shared.command.Command;

public record DeleteItemCommand(UUID itemId) implements Command {}
