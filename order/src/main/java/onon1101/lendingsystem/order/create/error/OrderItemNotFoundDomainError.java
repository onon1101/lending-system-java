package onon1101.lendingsystem.order.create.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class OrderItemNotFoundDomainError extends DomainError {

    public static final String CODE = "Order.ItemNotFound";

    public OrderItemNotFoundDomainError() {
        super(CODE, "The requested item does not exist.");
    }
}
