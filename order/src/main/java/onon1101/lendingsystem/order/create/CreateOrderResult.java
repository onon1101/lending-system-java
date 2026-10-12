package onon1101.lendingsystem.order.create;

import onon1101.lendingsystem.shared.command.CommandResult;

import java.time.LocalDate;
import java.util.UUID;

public record CreateOrderResult(
        UUID orderId,
        Status status,
        LocalDate plannedBorrowDate,
        LocalDate plannedReturnDate
) implements CommandResult {

    public enum Status {
        PENDING("pending");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
