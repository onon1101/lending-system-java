package onon1101.lendingsystem.order.reject;

import java.util.Objects;
import java.util.UUID;

public record RejectOrderCommand(
        UUID orderId
) {

    public RejectOrderCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}
