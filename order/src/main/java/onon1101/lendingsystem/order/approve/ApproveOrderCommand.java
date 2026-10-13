package onon1101.lendingsystem.order.approve;


import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ApproveOrderCommand(
        UUID orderId,
        Instant dueAt
) {
    public ApproveOrderCommand {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(dueAt, "dueAt must not be null");
    }
}
