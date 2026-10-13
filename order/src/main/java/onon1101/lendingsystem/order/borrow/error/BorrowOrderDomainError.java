package onon1101.lendingsystem.order.borrow.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class BorrowOrderDomainError extends DomainError {
    public static final String NOT_FOUND = "Order.NotFound";
    public static final String INVALID_STATUS = "Order.InvalidStatus";
    public static final String ITEM_UNAVAILABLE = "Order.ItemUnavailable";
    public static final String DUE_AT_EXPIRED = "Order.DueAtExpired";

    private BorrowOrderDomainError(String code, String message) {
        super(code, message);
    }

    public static BorrowOrderDomainError notFound() {
        return new BorrowOrderDomainError(
                NOT_FOUND, "The order was not found or is not accessible.");
    }

    public static BorrowOrderDomainError invalidStatus() {
        return new BorrowOrderDomainError(
                INVALID_STATUS, "The order cannot be borrowed in its current state.");
    }

    public static BorrowOrderDomainError itemUnavailable() {
        return new BorrowOrderDomainError(ITEM_UNAVAILABLE, "The item is not available.");
    }

    public static BorrowOrderDomainError dueAtExpired() {
        return new BorrowOrderDomainError(
                DUE_AT_EXPIRED, "The due time must be after the handover time.");
    }
}
