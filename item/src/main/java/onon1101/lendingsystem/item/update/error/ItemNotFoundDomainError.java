package onon1101.lendingsystem.item.update.error;

import onon1101.lendingsystem.configurations.domain.DomainError;

public final class ItemNotFoundDomainError extends DomainError {

    public ItemNotFoundDomainError() {
        super(
                "Item.NotFound",
                "The item does not exist or cannot be updated.");
    }
}
