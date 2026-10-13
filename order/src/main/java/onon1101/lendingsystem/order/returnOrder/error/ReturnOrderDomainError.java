package onon1101.lendingsystem.order.returnOrder.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class ReturnOrderDomainError extends DomainError {
    public static final String NOT_FOUND = "Order.NotFound";
    public static final String INVALID_STATUS = "Order.InvalidStatus";

    private ReturnOrderDomainError(String code, String message) {
        super(code, message);
    }

    public static ReturnOrderDomainError notFound() {
        return new ReturnOrderDomainError(
                NOT_FOUND, "The order was not found or is not accessible.");
    }

    public static ReturnOrderDomainError invalidStatus() {
        return new ReturnOrderDomainError(
                INVALID_STATUS, "The order cannot be returned in its current state.");
    }
}
