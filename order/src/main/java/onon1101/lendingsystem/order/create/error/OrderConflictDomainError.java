package onon1101.lendingsystem.order.create.error;

import onon1101.lendingsystem.shared.domain.DomainError;

public final class OrderConflictDomainError extends DomainError {

    public static final String CANNOT_BORROW_OWN_ITEM = "Order.CannotBorrowOwnItem";
    public static final String ITEM_UNAVAILABLE = "Order.ItemUnavailable";
    public static final String OPEN_REQUEST_EXISTS = "Order.OpenRequestExists";

    private OrderConflictDomainError(String code, String message) {
        super(code, message);
    }

    public static OrderConflictDomainError cannotBorrowOwnItem() {
        return new OrderConflictDomainError(
                CANNOT_BORROW_OWN_ITEM,
                "The borrower must not be the owner of the item.");
    }

    public static OrderConflictDomainError itemUnavailable() {
        return new OrderConflictDomainError(
                ITEM_UNAVAILABLE,
                "The item is not available for borrowing requests.");
    }

    public static OrderConflictDomainError openRequestExists() {
        return new OrderConflictDomainError(
                OPEN_REQUEST_EXISTS,
                "An open borrowing request already exists for this item and borrower.");
    }
}
