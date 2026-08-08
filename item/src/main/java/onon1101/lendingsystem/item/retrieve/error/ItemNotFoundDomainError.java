package onon1101.lendingsystem.item.retrieve.error;


import onon1101.lendingsystem.configurations.domain.DomainError;

public final class ItemNotFoundDomainError extends DomainError {

    public ItemNotFoundDomainError() {
        super(
                "Item.NotFound",
                "The requested item does not exist or is not visible.");
    }
}
