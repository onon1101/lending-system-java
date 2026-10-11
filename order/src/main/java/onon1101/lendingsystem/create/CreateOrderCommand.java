package onon1101.lendingsystem.create;

import onon1101.lendingsystem.shared.command.Command;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record CreateOrderCommand(
        UUID itemId,
        LocalDate plannedBorrowDate,
        LocalDate plannedReturnDate
) implements Command {

    public CreateOrderCommand {
        Objects.requireNonNull(itemId, "itemId must not be null.");
        Objects.requireNonNull(plannedBorrowDate, "plannedBorrowDate must not be null.");
        Objects.requireNonNull(plannedReturnDate, "plannedReturnDate must not be null.");
    }
}
