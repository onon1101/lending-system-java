package onon1101.lendingsystem.order.cancel;

import java.util.Objects;
import java.util.UUID;

public record CancelOrderCommand(UUID orderId) {

    public CancelOrderCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}
