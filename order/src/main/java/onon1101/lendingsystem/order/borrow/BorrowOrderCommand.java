package onon1101.lendingsystem.order.borrow;

import java.util.Objects;
import java.util.UUID;

public record BorrowOrderCommand(UUID orderId) {

    public BorrowOrderCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}
