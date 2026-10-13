package onon1101.lendingsystem.order.cancel.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class CancelOrderDomainError extends DomainError {
    public static final String NOT_FOUND = "Order.NotFound";
    public static final String INVALID_STATUS = "Order.InvalidStatus";

    private CancelOrderDomainError(String code, String message) {
        super(code, message);
    }

    public static CancelOrderDomainError notFound() {
        return new CancelOrderDomainError(
                NOT_FOUND, "The order was not found or is not accessible.");
    }

    public static CancelOrderDomainError invalidStatus() {
        return new CancelOrderDomainError(
                INVALID_STATUS, "The order cannot be cancelled in its current state.");
    }
}
