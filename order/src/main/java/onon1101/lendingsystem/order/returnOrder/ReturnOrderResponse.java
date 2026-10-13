package onon1101.lendingsystem.order.returnOrder;

import java.time.Instant;
import java.util.UUID;

public record ReturnOrderResponse(UUID orderId, String status, Instant returnedAt) {}
