package onon1101.lendingsystem.list;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ListedOrder(
        UUID orderId,
        UUID itemId,
        String itemName,
        UUID borrowerId,
        String borrowerUsername,
        UUID lenderId,
        String lenderUsername,
        String status,
        LocalDate plannedBorrowDate,
        LocalDate plannedReturnDate,
        Instant requestedAt
) {
}
