package onon1101.lendingsystem.order.returnOrder;

import java.util.Objects;
import java.util.UUID;

public record ReturnOrderCommand(UUID orderId) {

    public ReturnOrderCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}
