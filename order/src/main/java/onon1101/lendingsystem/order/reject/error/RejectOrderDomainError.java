package onon1101.lendingsystem.order.reject.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class RejectOrderDomainError extends DomainError {

    public static final String NOT_FOUND = "Order.NotFound";
    public static final String INVALID_STATUS = "Order.InvalidStatus";

    private RejectOrderDomainError(String code, String message) {
        super(code, message);
    }

    public static RejectOrderDomainError notFound() {
        return new RejectOrderDomainError(
                NOT_FOUND,
                "The order was not found or is not accessible."
        );
    }

    public static RejectOrderDomainError invalidStatus() {
        return new RejectOrderDomainError(
                INVALID_STATUS,
                "Only pending orders can be rejected."
        );
    }
}
