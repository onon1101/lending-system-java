package onon1101.lendingsystem.configurations.audit.eventAttributes;

import java.util.Objects;

public final class ItemCreationAuditEventAttribute
        implements AuditEventAttribute {

    private final String value;

    public ItemCreationAuditEventAttribute(String itemName) {
        this.value = Objects.requireNonNull(
                itemName,
                "Item name must not be null.");
    }

    @Override
    public String Key() {
        return "itemCreationRef";
    }

    @Override
    public String Value() {
        return value;
    }
}
