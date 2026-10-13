package onon1101.lendingsystem.order.cancel;

import java.time.Instant;
import java.util.UUID;

public record CancelOrderResponse(UUID orderId, String status, Instant closedAt) {}
