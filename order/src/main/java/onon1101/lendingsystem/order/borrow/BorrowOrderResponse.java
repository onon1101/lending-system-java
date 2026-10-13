package onon1101.lendingsystem.order.borrow;

import java.time.Instant;
import java.util.UUID;

public record BorrowOrderResponse(UUID orderId, String status, Instant borrowedAt) {}
