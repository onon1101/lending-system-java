package onon1101.lendingsystem.order.approve;

import java.time.Instant;
import java.util.UUID;

public record ApproveOrderResponse(
        UUID orderId,
        String status,
        Instant approvedAt,
        Instant dueAt
) {
}
