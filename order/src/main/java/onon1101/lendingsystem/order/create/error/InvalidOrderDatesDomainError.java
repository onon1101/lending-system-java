package onon1101.lendingsystem.order.create.error;


import onon1101.lendingsystem.shared.domain.DomainError;

public final class InvalidOrderDatesDomainError extends DomainError {

    public static final String CODE = "Order.InvalidDates";

    public InvalidOrderDatesDomainError(String message) {
        super(CODE, message);
    }
}
