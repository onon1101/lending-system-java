package onon1101.lendingsystem.order.reject;

import java.time.Instant;
import java.util.UUID;

public record RejectOrderResult(
        UUID orderId,
        String status,
        Instant closedAt
) {
}
