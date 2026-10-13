package onon1101.lendingsystem.order.approve.error;


import onon1101.lendingsystem.shared.domain.DomainError;

public final class ApproveOrderDomainError extends DomainError {

    public static final String NOT_FOUND = "Order.NotFound";
    public static final String INVALID_STATUS = "Order.InvalidStatus";
    public static final String ITEM_UNAVAILABLE = "Order.ItemUnavailable";
    public static final String ACTIVE_ORDER_EXISTS = "Order.ActiveOrderExists";
    public static final String INVALID_DUE_AT = "Order.InvalidDueAt";

    private ApproveOrderDomainError(String code, String message) {
        super(code, message);
    }

    public static ApproveOrderDomainError notFound() {
        return new ApproveOrderDomainError(
                NOT_FOUND,
                "The order was not found or is not accessible."
        );
    }

    public static ApproveOrderDomainError invalidStatus() {
        return new ApproveOrderDomainError(
                INVALID_STATUS,
                "Only pending orders can be approved."
        );
    }

    public static ApproveOrderDomainError itemUnavailable() {
        return new ApproveOrderDomainError(
                ITEM_UNAVAILABLE,
                "The item is not available."
        );
    }

    public static ApproveOrderDomainError activeOrderExists() {
        return new ApproveOrderDomainError(
                ACTIVE_ORDER_EXISTS,
                "An approved or borrowed order already exists for this item."
        );
    }

    public static ApproveOrderDomainError invalidDueAt() {
        return new ApproveOrderDomainError(
                INVALID_DUE_AT,
                "The due time must be after approval and request times."
        );
    }
}
