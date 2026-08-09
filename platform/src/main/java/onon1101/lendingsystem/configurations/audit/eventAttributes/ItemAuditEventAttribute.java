package onon1101.lendingsystem.configurations.audit.eventAttributes;

import java.util.Objects;

public class ItemAuditEventAttribute implements AuditEventAttribute{

    private final String value;

    public ItemAuditEventAttribute(String value) {
        Objects.requireNonNull(value, "value must not be null");
        this.value = value;
    }


    @Override
    public String Key() { return "itemRef"; }

    @Override
    public String Value() {return value;}
}
